package com.RahulSaiSamineni.cloudsim.utils.ProviderModels

import scala.beans.BeanProperty
import scala.jdk.CollectionConverters._


case class PaaSModel(var datacenters: List[DataCenterModel],
                     @BeanProperty var vmChars: VMModel
                    ) {

  def getDatacenters: java.util.List[DataCenterModel] = datacenters.asJava

  def setDatacenters(datacenters: java.util.List[DataCenterModel]): Unit = {
    this.datacenters = datacenters.asScala.toList
  }




  def this() = this(null, null)


}
