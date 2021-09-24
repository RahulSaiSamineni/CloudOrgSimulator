package com.RahulSaiSamineni.cloudsim

import java.lang.System.Logger.Level
import ch.qos.logback.classic
import com.RahulSaiSamineni.cloudsim.utils.ProviderModels.{IaaSModel, PaaSModel, SaaSModel}
import com.RahulSaiSamineni.cloudsim.utils.ServiceUtilities.ServiceUtils
import com.RahulSaiSamineni.custombuilder.CustomBuilder
import com.RahulSaiSamineni.cloudsim.utils.ProviderModels.IaaSModel
import com.RahulSaiSamineni.cloudsim.utils.ProviderUtilities.ProviderUtils._
import com.RahulSaiSamineni.cloudsim.utils.ServiceModels.{IaasServiceModel, PaasServiceModel, SaasServiceModel}
import com.RahulSaiSamineni.cloudsim.utils.ServiceModels.SaasServiceModel
import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.LazyLogging
import org.cloudbus.cloudsim.brokers.{DatacenterBroker, DatacenterBrokerSimple}
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudbus.cloudsim.datacenters.Datacenter
import org.cloudbus.cloudsim.hosts.Host
import org.cloudsimplus.builders.tables.CloudletsTableBuilder
import org.cloudsimplus.util.Log

import scala.jdk.CollectionConverters._


object Simulation5 extends App with LazyLogging {


  val iaasCon: Config = ConfigFactory.load("simulation_5/sim5_iaas.conf")

  //Log.setLevel(classic.Level.INFO)
  val saasCon: Config = ConfigFactory.load("simulation_5/sim5_saas.conf")
  val paasCon: Config = ConfigFactory.load("simulation_5/sim5_paas.conf")
  // Load all the provider configs
  val saas = loadDatacenterFromConfig2(saasCon.getConfig("simulation-five")).asInstanceOf[SaaSModel]
  val paas = loadDatacenterFromConfig2(paasCon.getConfig("simulation-five")).asInstanceOf[PaaSModel]
  val iaas = loadDatacenterFromConfig2(iaasCon.getConfig("simulation-five")).asInstanceOf[IaaSModel]
  // Load all the Client configs
  val iaasServModel = new IaasServiceModel("simulation_5/iaas_service.conf")
  val paasServModel = new PaasServiceModel("simulation_5/paas_service.conf")
  val saasServModel = new SaasServiceModel("simulation_5/saas_service.conf")
  val simulation = new CloudSim()
  // create the Datacenters and the topology
  val SaaSDataCenter = createSaasDataCenter(simulation, saas)
  val PaaSDataCenter = createPaasDataCenter(simulation, paas)
  logger.info(s"SaaS $SaaSDataCenter")
  val IaasDataCenter = createIaasDataCenter(simulation, iaas, iaasServModel)
  logger.info(s"PaaS $PaaSDataCenter")
  // load create a service util object
  val serviceUtils: ServiceUtils = ServiceUtils(paasServModel, iaasServModel, saasServModel)
  logger.info(s"IaaS $IaasDataCenter")
  configureNetworkTopology(simulation, SaaSDataCenter ::: PaaSDataCenter ::: IaasDataCenter)
  logger.info("The entities in this simulation are: $simulation.getEntityList.toString")
  // create the broker0
  // create VMs. 0 and null defines the control of the provider (that is, they do not have)
  // Iaas has full control of the sizes and the number of VMs they need and can create
  val iaasVM = serviceUtils.createVMList(0, null, "IaaS")
  // saas has no control of the VMs they need and can create. All the provider configs are passed
  val saasVM = serviceUtils.createVMList(saas.getVmChars.getVmNumber, saas.getVmChars, "SaaS")
  iaasVM.foreach(vm => vm.setHost(getRandomHostInDC(IaasDataCenter.head)))
  // paas has control of the number of VMs but not the configurations they need and can create. All the provider configs are passed
  val paasVM = serviceUtils.createVMList(0, paas.getVmChars, "PaaS")
  saasVM.foreach(vm => vm.setHost(getRandomHostInDC(SaaSDataCenter.head)))
  // load cloudlets using the service configs. add tasks
  val iaasCloudlets = serviceUtils.createCloudlets(null, "IaaS")
  paasVM.foreach(vm => vm.setHost(getRandomHostInDC(PaaSDataCenter.head)))
  val saasCloudlet = serviceUtils.createCloudlets(saas.getCloudletChars, "SaaS")
  val paasCloudlet = serviceUtils.createCloudlets(null, "PaaS")
  val broker5 = new DatacenterBrokerSimple(simulation)
  val finishedCloudletslt = broker5.getCloudletCreatedList

  broker5.submitVmList((iaasVM ::: saasVM ::: paasVM).asJava)
  broker5.setVmDestructionDelayFunction(vm => 10.0)
  broker5.submitCloudletList((iaasCloudlets ::: paasCloudlet ::: saasCloudlet).asJava)

  saasVM.map(println)

  simulation.start()

  def getRandomHostInDC(DC: Datacenter): Host = {
    DC.getHost(getRandomIndex(0, (DC.getHostList.size()) - 1))
  }
  new CustomBuilder(finishedCloudletslt).build()


}
