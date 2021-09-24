package com.RahulSaiSamineni.cloudsim.utils.ServiceModels

import com.typesafe.config.{Config, ConfigFactory}

//Created SaasServiceModel
class SaasServiceModel(simFile: String) {

  val config: Config = ConfigFactory.load(simFile)
  val cloudletConfig: Config = config.getObject("service.cloudlet-chars").toConfig

  val CLOUDLET_NUM: Int = cloudletConfig.getInt("number")
  val CLOUDLET_MIN: Int = cloudletConfig.getInt("minLength")
  val CLOUD_MAX: Int = cloudletConfig.getInt("maxLength")
  val CLOUDLET_RAM: Int = cloudletConfig.getInt("ram")


}
