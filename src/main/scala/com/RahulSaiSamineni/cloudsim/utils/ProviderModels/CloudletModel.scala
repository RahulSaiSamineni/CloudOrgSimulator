package com.RahulSaiSamineni.cloudsim.utils.ProviderModels

import java.beans.BeanProperty


case class CloudletModel(@BeanProperty var number: Int,
                         @BeanProperty var cloudletPes: Int,
                         @BeanProperty var minLength: Long,
                         @BeanProperty var maxLength: Long,
                         @BeanProperty var fileSize: Long,
                         @BeanProperty var outputSize: Long) {
  
  def this() = this(0, 0, 0, 0, 0, 0)

}
