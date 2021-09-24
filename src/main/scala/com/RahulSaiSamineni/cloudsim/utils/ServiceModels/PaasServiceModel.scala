package com.RahulSaiSamineni.cloudsim.utils.ServiceModels

import com.typesafe.config.{Config, ConfigFactory}




// Created PaaS Service Model to execute the services and it's specifications
class PaasServiceModel(simFile: String){

  val config: Config = ConfigFactory.load(simFile)
  val cloudletConfig: Config = config.getObject("service.cloudlet-chars").toConfig

  val VM_NUM: Int = config.getInt("service.vm.vm-number")

  val CLOUDLET_NUM: Int = cloudletConfig.getInt("number")
  val CLOUDLET_PES: Int = cloudletConfig.getInt("pes")
  val CLOUDLET_MIN: Int = cloudletConfig.getInt("minLength")
  val CLOUD_MAX: Int = cloudletConfig.getInt("maxLength")
  val CLOUDLET_FILE_SIZE: Int = cloudletConfig.getInt("fileSize")
  val CLOUDLET_OUTPUT_SIZE: Int = cloudletConfig.getInt("outputSize")
  val CLOUDLET_RAM: Int = cloudletConfig.getInt("ram")


}
