package com.RahulSaiSamineni.cloudsim.utils.ProviderUtilities

import com.RahulSaiSamineni.cloudsim.utils.ProviderModels.SwitchModel

import java.io.InvalidClassException
import com.typesafe.scalalogging.LazyLogging
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudbus.cloudsim.datacenters.network.NetworkDatacenter
import org.cloudbus.cloudsim.hosts.network.NetworkHost
import org.cloudbus.cloudsim.network.switches.{AggregateSwitch, EdgeSwitch, RootSwitch, Switch}

import scala.jdk.CollectionConverters._
import scala.reflect.runtime.universe._



object SwitchUtils extends LazyLogging {

  def connectRootSwitchToAggregate(rootSwitch: RootSwitch, aggregateSwitches: List[AggregateSwitch]) = {
    for (aggregateSwitch <- aggregateSwitches) {
      aggregateSwitch.getUplinkSwitches.add(rootSwitch)
      rootSwitch.getDownlinkSwitches.add(aggregateSwitch)
    }
  }

  def createEdgeSwitch(sim: CloudSim, edgeSwitchModel: SwitchModel, datacenter: NetworkDatacenter): List[EdgeSwitch] = {
    val edgeSwitches = (0 until edgeSwitchModel.number).map {
      i =>
        val edgeSwitch = createSwitch[EdgeSwitch](
          sim,
          datacenter,
          edgeSwitchModel.numPorts,
          edgeSwitchModel.bw,
          edgeSwitchModel.switchingDelay,
        )
        // Add to dc
        datacenter.addSwitch(edgeSwitch)
        // Add the edgeswitch to the list of edgeswitches
        edgeSwitch
    }.toList
    edgeSwitches
  }


  def createSwitch[T <: Switch](sim: CloudSim, datacenter: NetworkDatacenter, ports: Int, bw: Long, switchingDelay: Double)(implicit b: TypeTag[T]): T = {
    val switch = (
      b.tpe.toString match {
        case "org.cloudbus.cloudsim.network.switches.EdgeSwitch" => new EdgeSwitch(sim, datacenter)
        case "org.cloudbus.cloudsim.network.switches.AggregateSwitch" => new AggregateSwitch(sim, datacenter)
        case "org.cloudbus.cloudsim.network.switches.RootSwitch" => new RootSwitch(sim, datacenter)
        case _ => throw new InvalidClassException(s"Not a proper switch")
      }
      ).asInstanceOf[T]

    switch.setPorts(ports)
    switch.setDownlinkBandwidth(bw)
    switch.setUplinkBandwidth(bw)
    switch.setSwitchingDelay(switchingDelay)

    switch
  }


  def createAggregateSwitch(sim: CloudSim, aggregateSwitchModel: SwitchModel, datacenter: NetworkDatacenter): List[AggregateSwitch] = {
    val aggregateSwitches = (0 until aggregateSwitchModel.number).map {
      i =>
        val aggregateSwitch = createSwitch[AggregateSwitch](
          sim,
          datacenter,
          aggregateSwitchModel.numPorts,
          aggregateSwitchModel.bw,
          aggregateSwitchModel.switchingDelay,
        )
        // add to dc
        datacenter.addSwitch(aggregateSwitch)
        // add the switch object to the list of aggregateSwitches
        aggregateSwitch
    }.toList
    aggregateSwitches
  }



  def createRootSwitch(sim: CloudSim, rootSwitchModel: SwitchModel, datacenter: NetworkDatacenter): RootSwitch = {
    val rootSwitch = createSwitch[RootSwitch](sim, datacenter, rootSwitchModel.numPorts, rootSwitchModel.bw, rootSwitchModel.switchingDelay)
    logger.info(rootSwitch.getClass.toString)
    rootSwitch
  }


  def connectEdgeSwitchesToHost(datacenter: NetworkDatacenter, edgeSwitches: List[EdgeSwitch], edgeSwitchModel: SwitchModel): Unit = {
    datacenter.getHostList[NetworkHost].asScala.foreach { host =>
      val switchNum = getSwitchIndex(host, edgeSwitchModel.numPorts)
      logger.info(s"Switch Num: $switchNum")
      edgeSwitches(switchNum).connectHost(host)
    }
  }


  def getSwitchIndex(host: NetworkHost, switchPorts: Int): Int = Math.round(host.getId % Int.MaxValue) / switchPorts

  def connectAggregateSwitchToEdgeSwitch(edgeSwitches: List[EdgeSwitch], aggregateSwitches: List[AggregateSwitch]) = {
    edgeSwitches.zipWithIndex.foreach { case (edgeSwitch, index) => {
      val aggregateSwitch = aggregateSwitches(index / aggregateSwitches.head.getPorts)
      edgeSwitch.getUplinkSwitches.add(aggregateSwitch)
      aggregateSwitch.getDownlinkSwitches.add(edgeSwitch)
    }

    }

  }
}
