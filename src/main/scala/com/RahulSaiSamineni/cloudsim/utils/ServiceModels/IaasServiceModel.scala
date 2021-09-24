package com.RahulSaiSamineni.cloudsim.utils.ServiceModels

import com.RahulSaiSamineni.cloudsim.utils.ProviderModels.HostModel
import com.typesafe.config.{Config, ConfigFactory}

import scala.jdk.CollectionConverters._

//Creating IaasServiceModel
class IaasServiceModel(simFile: String)  {

  val config: Config = ConfigFactory.load(simFile)
  val vmConfig: Config = config.getObject("service.vm").toConfig
  val cloudletConfig: Config = config.getObject("service.cloudlet-chars").toConfig

  val CLOUDLET_NUM: Int = cloudletConfig.getInt("number")
  val CLOUDLET_PERS: Int = cloudletConfig.getInt("pes")
  val CLOUDLET_MIN: Int = cloudletConfig.getInt("minLength")
  val CLOUD_MAX: Int = cloudletConfig.getInt("maxLength")
  val CLOUDLET_FILE_SIZE: Int = cloudletConfig.getInt("fileSize")
  val CLOUDLET_OUTPUT_SIZE: Int = cloudletConfig.getInt("outputSize")
  val CLOUDLET_RAM: Int = cloudletConfig.getInt("ram")



//Iaas has Control over VM and Hosts Machines
  val VM_NUMBER: Int = vmConfig.getInt("vm-number")
  val VM_MIPS: Int = vmConfig.getInt("vm-mips")
  val VM_SIZE: Int = vmConfig.getInt("vm-size")
  val VM_RAM: Int = vmConfig.getInt("vm-ram")
  val VM_BW: Int = vmConfig.getInt("vm-bw")
  val VM_PES: Int = vmConfig.getInt("vm-pes")
  val VM_VMM: String = vmConfig.getString("vm-vmm")


}
