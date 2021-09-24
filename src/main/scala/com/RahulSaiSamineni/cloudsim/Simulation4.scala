package com.RahulSaiSamineni.cloudsim

import com.RahulSaiSamineni.custombuilder.CustomBuilder
import com.RahulSaiSamineni.cloudsim.utils.ProviderUtilities.ProviderUtils._
import com.RahulSaiSamineni.cloudsim.utils.ServiceModels.IaasServiceModel
import com.RahulSaiSamineni.cloudsim.utils.ServiceUtilities.SimpleJob
import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.LazyLogging
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple
import org.cloudbus.cloudsim.core.CloudSim

import scala.jdk.CollectionConverters._


object Simulation4 extends LazyLogging {

  def main(args: Array[String]): Unit = {
    val con: Config = ConfigFactory.load("simulation4.conf")
    val simConfig = con.getConfig("simulation-four")
    val iaasServiceModel = new IaasServiceModel("service1.conf")
    logger.info(s"${simConfig.getString("description")}")
    val sim = new CloudSim()
    val datacenterList = loadDatacenterFromConfig(simConfig)
    val dC = createDataCenters(sim, datacenterList)
    val broker4 = new DatacenterBrokerSimple(sim)
    val Job = new SimpleJob(sim, broker4, iaasServiceModel)
    broker4.submitVmList(Job.createVMList.asJava)
    broker4.submitCloudletList(Job.createCloudletSimpleList.asJava)
    sim.start()
    val finishedCloudletslt = broker4.getCloudletFinishedList
    new CustomBuilder(finishedCloudletslt).build()
  }
}
