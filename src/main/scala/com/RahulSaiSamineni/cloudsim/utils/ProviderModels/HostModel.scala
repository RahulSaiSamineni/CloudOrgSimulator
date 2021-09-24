package com.RahulSaiSamineni.cloudsim.utils.ProviderModels

import scala.beans.BeanProperty



case class HostModel(@BeanProperty var number: Int,
                     @BeanProperty var ram: Long,
                     @BeanProperty var storage: Long,
                     @BeanProperty var bw: Long,
                     @BeanProperty var mips: Double,
                     @BeanProperty var cores: Int,
                     @BeanProperty var vmScheduler: String) {
  def this() = this(0, 0, 0, 0, 0, 0, "")
}
