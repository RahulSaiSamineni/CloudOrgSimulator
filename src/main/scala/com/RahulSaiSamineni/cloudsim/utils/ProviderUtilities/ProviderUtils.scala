package com.RahulSaiSamineni.cloudsim.utils.ProviderUtilities

import com.RahulSaiSamineni.cloudsim.utils.ProviderModels.{CloudletModel, DataCenterModel, HostModel, IaaSModel, PaaSModel, SaaSModel, SwitchModel, VMModel}
import com.RahulSaiSamineni.cloudsim.utils.ServiceModels.IaasServiceModel
import com.RahulSaiSamineni.cloudsim.utils.ProviderModels._
import com.typesafe.config.{Config, ConfigBeanFactory}
import com.typesafe.scalalogging.LazyLogging
import org.cloudbus.cloudsim.allocationpolicies._
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudbus.cloudsim.datacenters.network.NetworkDatacenter
import org.cloudbus.cloudsim.datacenters.{Datacenter, DatacenterSimple}
import org.cloudbus.cloudsim.hosts.Host
import org.cloudbus.cloudsim.hosts.network.NetworkHost
import org.cloudbus.cloudsim.network.topologies.{BriteNetworkTopology, NetworkTopology}
import org.cloudbus.cloudsim.resources.{Pe, PeSimple}
import org.cloudbus.cloudsim.schedulers.vm.{VmSchedulerSpaceShared, VmSchedulerTimeShared}
import SwitchUtils._

import scala.jdk.CollectionConverters._
import scala.util.Random


object ProviderUtils extends LazyLogging {


    // Mapping random host of Particular Data Center to VM
    //Param Begin will be 0
    // Param end will be hosts number
    //returning random interval
  def getRandomIndex(start: Int, end: Int): Int = {
    val r = new Random
    start + r.nextInt((end - start) + 1)
  }

    //To Create a datacentermodel object using config.
  def loadDatacenterFromConfig2(conf1: Config) = {
    val pmodel = conf1.getString("model-type") match {
      case "IaaS" => {
        logger.trace(s"Iaas loading $conf1")
        IaaSModel(loadDatacenterFromConfig(conf1))
      }
      case "SaaS" => {
        logger.trace(s"Saas loading $conf1")
        SaaSModel(loadDatacenterFromConfig(conf1), loadVmFromConfig(conf1), loadCloudletsFromConfig(conf1))
      }
      case "PaaS" => {
        logger.trace(s"Paas loading $conf1")
        PaaSModel(loadDatacenterFromConfig(conf1), loadVmFromConfig(conf1))
      }

    }
    pmodel
  }


    //Creating Cloudlet from Config
    //returns cloudletmodel
  def loadCloudletsFromConfig(conf1: Config): CloudletModel =
    ConfigBeanFactory.create(conf1.getConfig("cloudlet-chars"), classOf[CloudletModel])



    //Creates VMModel from config
  def loadVmFromConfig(conf1: Config): VMModel = {
    ConfigBeanFactory.create(conf1.getConfig("vm-chars"), classOf[VMModel])
  }


    //Creates Datacenter from config
  def loadDatacenterFromConfig(conf1: Config): List[DataCenterModel] = {
    logger.trace(s"funct: loadDataCenterFromConfig(${conf1})")
    conf1
      .getConfigList("datacenters")
      .asScala.map {
      ConfigBeanFactory.create(_, classOf[DataCenterModel])
    }.toList
  }

    //Creating a network using Topology brite
  def configureNetworkTopology(simulation: CloudSim, datacenterList: List[Datacenter]): Unit = {
    val networkTopology: NetworkTopology = BriteNetworkTopology.getInstance("topology.brite")
    simulation.setNetworkTopology(networkTopology)
    networkTopology.mapNode(datacenterList.head.getId, 0)
    networkTopology.mapNode(datacenterList.tail.head.getId, 1)
    networkTopology.mapNode(datacenterList.tail.tail.head.getId, 2)
  }

//Creates DataCenterList to Simulators
  def createPaasDataCenter(simulation: CloudSim, paasCenterList: PaaSModel): List[Datacenter] = {
    logger.trace(s"Func: createPaas($simulation, $paasCenterList")
    val dataCenters: List[DataCenterModel] = paasCenterList.datacenters
    createDataCenters(simulation, dataCenters)
  }

//Creates DataCenter to simulate Saas
  def createSaasDataCenter(simulation: CloudSim, SaaSCenterList: SaaSModel): List[Datacenter] = {
    logger.trace(s"Func: createSaas($simulation, $SaaSCenterList")
    val dataCenters = SaaSCenterList.datacenters
    createDataCenters(simulation, dataCenters)
  }

//Created DataCenters
  def createDataCenters(simulation: CloudSim, dataCenterList: List[DataCenterModel]): List[Datacenter] = {
    logger.trace(s"Func: createIaasDataCenters(${simulation}, ${dataCenterList})")

    dataCenterList.map { dc =>
      val host_List = createHostList(dc.hosts)

      // Check type and create that DataCenter
      val datacenterSimple = dc.dcType match {
        case "Simple" => new DatacenterSimple(simulation, host_List.asJava)
        case "Network" => new NetworkDatacenter(simulation, host_List.asJava, getVmAllocationPolicy(dc.vmAllocationPolicy))
        case _ => new DatacenterSimple(simulation, host_List.asJava)
      }

      // Set all the costs
      datacenterSimple.getCharacteristics
        .setCostPerBw(dc.costPerBw)
        .setCostPerMem(dc.costPerMem)
        .setCostPerSecond(dc.costPerSecond)
        .setCostPerStorage(dc.costPerStorage)

      // If network datacenter, also build the network switches
      if (dc.dcType == "Network")
        createNetwork(
          simulation,
          datacenterSimple.asInstanceOf[NetworkDatacenter],
          dc.edgeSwitch,
          dc.rootSwitch,
          dc.aggregateSwitch
        )
      // return the datacenter to the Map
      datacenterSimple
    } //end of map

  }

    //Setting the VM Allocation Policy using choices
  def getVmAllocationPolicy(policy: String): VmAllocationPolicy = {
    policy match {
      case "FirstFit" => new VmAllocationPolicyFirstFit()
      case "BestFit" => new VmAllocationPolicyBestFit()
      case "WorstFit" => new VmAllocationPolicyWorstFit()
      case _ => new VmAllocationPolicySimple()
    }
  }

//Creating a List of Hosts
  def createHostList(hostList: List[HostModel]): List[Host] = {
    logger.trace(s"Func: createHostList(${hostList})")

    // Create a single dimensional list of hosts
    hostList.flatMap { h =>
      (1 to h.number).map { _ =>
        createHost(h.cores, h.mips, h.ram, h.storage, h.bw, h.vmScheduler)
      }
    }
  }

 // Creating Host as per the parameters
  def createHost(cores: Int, mips: Double, ram: Long, storage: Long, bw: Long, vmScheduler: String): Host = {
    val perList = (1 to cores).map { _ =>
      new PeSimple(mips).asInstanceOf[Pe]
    }.toList

    val host = new NetworkHost(ram, bw, storage, perList.asJava)
    host.setVmScheduler(vmScheduler match {
      case "SpaceShared" =>
        logger.info("Choosing SpaceShared")
        new VmSchedulerSpaceShared()
      case "TimeShared" =>
        logger.info("Choosing TimeShared")
        new VmSchedulerTimeShared()
      case _ => new VmSchedulerTimeShared()
    })
  }

    // Adding switches to topology
  def createNetwork(sim: CloudSim, datacenter: NetworkDatacenter, edgeSwitchModel: SwitchModel, rootSwitchModel: SwitchModel, aggregateSwitchModel: SwitchModel) = {
    val edgeSwitches = createEdgeSwitch(sim, edgeSwitchModel, datacenter)
    logger.info(s"$edgeSwitches")
    val rootSwitch = createRootSwitch(sim, rootSwitchModel, datacenter)
    val aggregateSwitches = createAggregateSwitch(sim, aggregateSwitchModel, datacenter)

    // We have to connect the Root switch to aggregate switches
    connectRootSwitchToAggregate(rootSwitch, aggregateSwitches)
    // connect all host to edgeSwitches
    connectEdgeSwitchesToHost(datacenter, edgeSwitches, edgeSwitchModel)

  }

//Creates a DataCenter to Simulate Iaas
  def createIaasDataCenter(simulation: CloudSim, iaasCenterList: IaaSModel, iaasServiceModel: IaasServiceModel): List[Datacenter] = {
    logger.trace(s"Func: createSaas($simulation, $iaasCenterList")
    val dataCenters: List[DataCenterModel] = iaasCenterList.datacenters
    // total control
    createDataCenters(simulation, dataCenters)
  }
}
