package com.RahulSaiSamineni.cloudsim.utils.ServiceUtilities

import com.RahulSaiSamineni.cloudsim.utils.ProviderModels.{CloudletModel, VMModel}
import com.RahulSaiSamineni.cloudsim.utils.ServiceModels.{IaasServiceModel, PaasServiceModel, SaasServiceModel}
import com.RahulSaiSamineni.cloudsim.utils.ProviderModels.CloudletModel
import com.RahulSaiSamineni.cloudsim.utils.ServiceModels.SaasServiceModel
import com.typesafe.scalalogging.LazyLogging
import org.cloudbus.cloudsim.cloudlets.network.{CloudletExecutionTask, NetworkCloudlet}
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelFull
import org.cloudbus.cloudsim.vms.{Vm, VmSimple}
import org.cloudsimplus.autoscaling.{HorizontalVmScaling, HorizontalVmScalingSimple}

import scala.util.Random


//class that is used to instanciate to create VM and cloulets
//3 service models are passed as 3 parameters
case class ServiceUtils(val paasServiceModel: PaasServiceModel,
                        val iaasServiceModel: IaasServiceModel,
                        val saasServiceModel: SaasServiceModel) extends LazyLogging {


  //Creating List of VMs
  def createVMList(number: Int = 0, vmModel1: VMModel = null, model: String = ""): List[Vm] =
    model match {
      // If the selected case is Iaas, the VM is chosen from user config
      case "IaaS" => (1 to iaasServiceModel.VM_NUMBER).map(_ => createVm()).toList
      // If the selected case is Saas, from data Center their numbers are chosen
      case "SaaS" if (number != 0 && vmModel1 != null) => (1 to number).map(_ => createVm(vmModel1)).toList
      // If the selected case is Paas, VMs are chose from user except their size
      case "PaaS" if (vmModel1 != null) => (1 to paasServiceModel.VM_NUM).map(_ => createVm(vmModel1)).toList
      case _ => throw new RuntimeException("No Model/number specified for Saas/Paas")
    }

 //Creating single VM
  def createVm(): Vm = {
    new VmSimple(iaasServiceModel.VM_MIPS, iaasServiceModel.VM_PES)
      .setRam(iaasServiceModel.VM_RAM)
      .setBw(iaasServiceModel.VM_BW)
      .setSize(iaasServiceModel.VM_SIZE)
  }

  // Create VM for PaaS and IaaS
  def createVm(vmModel1: VMModel): Vm = {
    new VmSimple(vmModel1.vmMips, vmModel1.getVmPes)
      .setRam(vmModel1.vmRam)
      .setBw(vmModel1.vmBw)
      .setSize(vmModel1.vmSize)
  }

 //Cloudlets creation for 3 models
  def createCloudlets(cloudletModel: CloudletModel = null, model: String): List[NetworkCloudlet] =
    model match {
      case "SaaS" if (cloudletModel != null) => (1 to saasServiceModel.CLOUDLET_NUM).map(id => createCloudlet(id, cloudletModel)).toList
      case "PaaS" => (1 to paasServiceModel.CLOUDLET_NUM).map(id => createCloudlet(id, model)).toList
      case "IaaS" => (1 to iaasServiceModel.CLOUDLET_NUM).map(id => createCloudlet(id, model)).toList
      case _ => throw new Exception("Wrong model passed while creating cloudlets. SaaS/PaaS/IaaS")
    }

//Creating single cloudlet for a specified model
  def createCloudlet(id: Int, model: String): NetworkCloudlet = {
    model match {
      case "PaaS" => {
        val c = new NetworkCloudlet(id + saasServiceModel.CLOUDLET_NUM, getRandomLength(paasServiceModel.CLOUDLET_MIN, paasServiceModel.CLOUD_MAX), paasServiceModel.CLOUDLET_PES)
          .setFileSize(paasServiceModel.CLOUDLET_FILE_SIZE)
          .setUtilizationModel(new UtilizationModelFull())
          .setOutputSize(paasServiceModel.CLOUDLET_OUTPUT_SIZE).asInstanceOf[NetworkCloudlet]
        c.addTask(new CloudletExecutionTask(id, getRandomLength(paasServiceModel.CLOUDLET_MIN, paasServiceModel.CLOUD_MAX)))
      }
      case "IaaS" => {
        val c = new NetworkCloudlet(id + saasServiceModel.CLOUDLET_NUM + paasServiceModel.CLOUDLET_NUM, getRandomLength(iaasServiceModel.CLOUDLET_MIN, iaasServiceModel.CLOUD_MAX), iaasServiceModel.CLOUDLET_PERS)
          .setFileSize(iaasServiceModel.CLOUDLET_FILE_SIZE)
          .setUtilizationModel(new UtilizationModelFull())
          .setOutputSize(iaasServiceModel.CLOUDLET_OUTPUT_SIZE).asInstanceOf[NetworkCloudlet]
        c.addTask(new CloudletExecutionTask(id, getRandomLength(paasServiceModel.CLOUDLET_MIN, paasServiceModel.CLOUD_MAX)))
      }
    }

  }

//Creating a Specific cloudlet when cloudletModel1 is passed as parameter
  def createCloudlet(id: Int, cloudletModel1: CloudletModel): NetworkCloudlet = {
    val c = new NetworkCloudlet(id, getRandomLength(cloudletModel1.minLength, cloudletModel1.maxLength), cloudletModel1.cloudletPes)
      .setFileSize(cloudletModel1.fileSize)
      .setUtilizationModel(new UtilizationModelFull())
      .setOutputSize(cloudletModel1.outputSize).asInstanceOf[NetworkCloudlet]
    c.addTask(new CloudletExecutionTask(id, getRandomLength(paasServiceModel.CLOUDLET_MIN, paasServiceModel.CLOUD_MAX)))
  }

//method for randomlength of cloudlets
  def getRandomLength(Begin: Long, end: Long): Long = {
    val rnd = new Random
    Begin + rnd.nextLong((end - Begin) + 1)
  }

//Creating a list of Scalable VMs
  def createInitialScalableVms(vmModel1: VMModel): List[Vm] =
    (1 to vmModel1.vmNumber).map { _ =>
      val vm: Vm = createVm(vmModel1)
      createHorizontalVmScaling(vm, vmModel1)

      //gather the VM
      vm
    }.toList

// HorizontalVM Scaling When cpu id Greater than 70%
  def createHorizontalVmScaling(vm: Vm, vmModel1: VMModel): Unit = {
    val horizontalVmScaling: HorizontalVmScaling = new HorizontalVmScalingSimple()
    horizontalVmScaling
      .setVmSupplier(() => createVm(vmModel1))
      .setOverloadPredicate(isVmOverloaded)
    vm.setHorizontalScaling(horizontalVmScaling)
  }

 //checking the load of CPU
  def isVmOverloaded(vm: Vm): Boolean = {
    vm.getCpuPercentUtilization > 0.7
  }

 // Execution Task for Networkcloudlet
  def addExecutionTask(networkCloudlet1: NetworkCloudlet, model: String) = {
    logger.info(s"Adding executionTask $networkCloudlet1, $model")
    val executionTask: CloudletExecutionTask = new CloudletExecutionTask(
      networkCloudlet1.getTasks.size(), networkCloudlet1.getLength)
    model match {
      case "IaaS" =>
        executionTask.setMemory(iaasServiceModel.CLOUDLET_RAM)
        networkCloudlet1.addTask(executionTask)
      case "PaaS" =>
        executionTask.setMemory(paasServiceModel.CLOUDLET_RAM)
        networkCloudlet1.addTask(executionTask)
      case "SaaS" =>
        executionTask.setMemory(saasServiceModel.CLOUDLET_RAM)
        networkCloudlet1.addTask(executionTask)
    }

  }

}
