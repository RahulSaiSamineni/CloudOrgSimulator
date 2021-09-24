package com.RahulSaiSamineni.cloudsim

import com.RahulSaiSamineni.custombuilder.CustomBuilder
import com.RahulSaiSamineni.cloudsim.utils.ProviderUtilities.ProviderUtils._
import com.RahulSaiSamineni.cloudsim.utils.ServiceModels.IaasServiceModel
import com.RahulSaiSamineni.cloudsim.utils.ServiceUtilities.SimpleJob
import com.RahulSaiSamineni.cloudsim.utils.ServiceModels.PaasServiceModel
import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.LazyLogging
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudsimplus.builders.tables.CloudletsTableBuilder

import scala.jdk.CollectionConverters._


object Simulation2 extends LazyLogging {

  def main(args: Array[String]): Unit = {
    val con: Config = ConfigFactory.load("simulation2.conf")
    val simConfig = con.getConfig("simulation-two")
    val iaasServiceModel = new IaasServiceModel("service1.conf")
    logger.info(s"${simConfig.getString("description")}")
    val sim = new CloudSim()
    val datacenterList = loadDatacenterFromConfig(simConfig)
    createDataCenters(sim, datacenterList)
    val broker1 = new DatacenterBrokerSimple(sim)
    val simpleJob1 = new SimpleJob(sim, broker1, iaasServiceModel)
    broker1.submitVmList(simpleJob1.createVMList.asJava)
    broker1.submitCloudletList(simpleJob1.createCloudletSimpleList.asJava)
    sim.start()
    val finishedCloudletslt = broker1.getCloudletFinishedList
    new CustomBuilder(finishedCloudletslt).build()
  }
}
