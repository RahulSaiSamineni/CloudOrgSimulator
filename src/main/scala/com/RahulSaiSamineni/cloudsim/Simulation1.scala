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


object Simulation1 extends LazyLogging {

  def main(args: Array[String]): Unit = {
    val conf: Config = ConfigFactory.load("simulation1.conf")
    val simConfig = conf.getConfig("simulation-one")
    val iaasServiceModel = new IaasServiceModel("service1.conf")
    logger.info(s"${simConfig.getString("description")}")
    val sim = new CloudSim()
    val dcModelList = loadDatacenterFromConfig(simConfig)
    val dcList = createDataCenters(sim, dcModelList)
    val broker0 = new DatacenterBrokerSimple(sim)
    val job = new SimpleJob(sim, broker0, iaasServiceModel)
    broker0.submitVmList(job.createVMList.asJava)
    broker0.submitCloudletList(job.createCloudletSimpleList.asJava)
    sim.start()
    val finishedCloudletsLt = broker0.getCloudletFinishedList
    new CustomBuilder(finishedCloudletsLt).build()
  }
}
