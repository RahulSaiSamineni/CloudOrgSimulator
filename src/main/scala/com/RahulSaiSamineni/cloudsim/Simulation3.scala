package com.RahulSaiSamineni.cloudsim

import com.RahulSaiSamineni.custombuilder.CustomBuilder
import com.RahulSaiSamineni.cloudsim.utils.ProviderUtilities.ProviderUtils._
import com.RahulSaiSamineni.cloudsim.utils.ServiceModels.IaasServiceModel
import com.RahulSaiSamineni.cloudsim.utils.ServiceUtilities.SimpleJob
import com.typesafe.config.{Config, ConfigFactory}
import com.typesafe.scalalogging.LazyLogging
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudsimplus.builders.tables.CloudletsTableBuilder
import com.RahulSaiSamineni.cloudsim.utils.ServiceUtilities._

import scala.jdk.CollectionConverters._


object Simulation3 extends LazyLogging {

  def main(args: Array[String]): Unit = {
    val con: Config = ConfigFactory.load("simulation3.conf")
    val simConfig = con.getConfig("simulation-three")
    val iaasServiceModel = new IaasServiceModel("service1.conf")
    logger.info(s"${simConfig.getString("description")}")
    val sim = new CloudSim()
    val datacenterList = loadDatacenterFromConfig(simConfig)
    val dataCenters2 = createDataCenters(sim, datacenterList)
    val broker3 = new DatacenterBrokerSimple(sim)
    val Job = new SimpleJob(sim, broker3, iaasServiceModel)
    broker3.submitVmList(Job.createVMList.asJava)
    broker3.submitCloudletList(Job.createCloudletSimpleList.asJava)
    sim.start()
    val finishedCloudletslt = broker3.getCloudletFinishedList
    new CustomBuilder(finishedCloudletslt).build()
  }
}
