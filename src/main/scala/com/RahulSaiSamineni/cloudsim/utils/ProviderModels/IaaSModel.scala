package com.RahulSaiSamineni.cloudsim.utils.ProviderModels

import scala.jdk.CollectionConverters._



case class IaaSModel(var datacenters: List[DataCenterModel]) {

  def getDatacenters: java.util.List[DataCenterModel] = datacenters.asJava

  def setDatacenters(datacenters: java.util.List[DataCenterModel]): Unit = {
    this.datacenters = datacenters.asScala.toList
  }


}

