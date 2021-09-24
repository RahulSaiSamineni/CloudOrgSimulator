package com.RahulSaiSamineni.cloudsim.utils.ProviderModels

import scala.beans.BeanProperty



class SwitchModel(@BeanProperty var number: Int,
                  @BeanProperty var numPorts: Int,
                  @BeanProperty var bw: Long,
                  @BeanProperty var switchingDelay: Long) {


  def this() = this(0, 0, 0, 0)

}
