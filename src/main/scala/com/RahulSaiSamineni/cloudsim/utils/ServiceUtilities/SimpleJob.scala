package com.RahulSaiSamineni.cloudsim.utils.ServiceUtilities

import com.RahulSaiSamineni.cloudsim.utils.ServiceModels.IaasServiceModel
import com.RahulSaiSamineni.cloudsim.ExampleSimulator.{cloudlet_length, cloudlet_pes, vm_pes}
import com.RahulSaiSamineni.cloudsim.utils.ProviderModels.CloudletModel
import com.RahulSaiSamineni.cloudsim.utils.ServiceModels.PaasServiceModel
import org.cloudbus.cloudsim.brokers.{DatacenterBroker, DatacenterBrokerSimple}
import org.cloudbus.cloudsim.cloudlets.network.NetworkCloudlet
import org.cloudbus.cloudsim.cloudlets.{Cloudlet, CloudletSimple}
import org.cloudbus.cloudsim.core.Simulation
import org.cloudbus.cloudsim.utilizationmodels.{UtilizationModelDynamic, UtilizationModelFull}
import org.cloudbus.cloudsim.vms.{Vm, VmSimple}


class SimpleJob(var simulation: Simulation,
                var broker0: DatacenterBroker,
                val iaasServiceModel: IaasServiceModel) {


  def createVMList: List[Vm] =
    (1 to iaasServiceModel.VM_NUMBER).map(_ => new VmSimple(iaasServiceModel.VM_MIPS, iaasServiceModel.VM_PES)
      .setRam(iaasServiceModel.VM_RAM)
      .setBw(iaasServiceModel.VM_BW)
      .setSize(iaasServiceModel.VM_SIZE)).toList



  def createCloudletSimpleList: List[Cloudlet] = {
    val utilizationModel: UtilizationModelFull = new UtilizationModelFull()
    (1 to iaasServiceModel.CLOUDLET_NUM).map { _ =>
      new CloudletSimple(iaasServiceModel.CLOUD_MAX, iaasServiceModel.CLOUDLET_PERS, utilizationModel)
    }.toList
  }







}
