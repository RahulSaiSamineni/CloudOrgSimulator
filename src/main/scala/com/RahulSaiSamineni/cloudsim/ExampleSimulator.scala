
package com.RahulSaiSamineni.cloudsim

import org.cloudbus.cloudsim.allocationpolicies.VmAllocationPolicyBestFit
import org.cloudbus.cloudsim.brokers.DatacenterBrokerSimple
import org.cloudbus.cloudsim.cloudlets.CloudletSimple
import org.cloudbus.cloudsim.core.CloudSim
import org.cloudbus.cloudsim.datacenters.network.NetworkDatacenter
import org.cloudbus.cloudsim.hosts.HostSimple
import org.cloudbus.cloudsim.resources.{Pe, PeSimple}
import org.cloudbus.cloudsim.utilizationmodels.UtilizationModelDynamic
import org.cloudbus.cloudsim.vms.VmSimple
import org.cloudsimplus.builders.tables.CloudletsTableBuilder

import scala.jdk.CollectionConverters._

/**
 *
 */
object ExampleSimulator extends App {

  val SIM = "examplesimulator"
  val hosts = 2
  val hosts_pes = 8
  val vms = 2
  val vm_pes = 5
  val dcs = 1
  val cloudlets = 6
  val cloudlet_pes = 2
  val cloudlet_length = 20000
  val simulation = new CloudSim()


  val bscex = basicExample

  // Initialize the datacenters for this simulation


  /**
   *
   */
  def basicExample = {
    val dcenter0 = createDataCenter
    val bk0 = new DatacenterBrokerSimple(simulation)
    val vmList = createVM
    val cloudletList = createCloudlets
    bk0.submitVmList(vmList.asJava)
    bk0.submitCloudletList(cloudletList.asJava)
    simulation.start()
    val finishedCloudlets = bk0.getCloudletFinishedList
    new CloudletsTableBuilder(finishedCloudlets).build()
  }

  /**
   *
   * @return
   */
  def createVM = {
    (1 to vms).map(_ => new VmSimple(1000, vm_pes)
      .setRam(512)
      .setBw(1000)
      .setSize(20000)).toList
  }

  def createCloudlets = {
    val utilizationModel: UtilizationModelDynamic = new UtilizationModelDynamic(0.5)
    (1 to cloudlets).
      map { _ =>
        new CloudletSimple(cloudlet_length, cloudlet_pes, utilizationModel)
      }
      .toList
  }

  /**
   *
   * @return
   */
  def createDataCenter = {
    val hostList = (1 to hosts).map(_ => createHost).toList
    new NetworkDatacenter(simulation, hostList.asJava, new VmAllocationPolicyBestFit())
  }

  /**
   *
   * @return
   */
  def createHost = {
    val peList = (1 to hosts_pes).map { _ =>
      new PeSimple(1000).asInstanceOf[Pe]
    }.toList
    val hosts_ram: Long = 2048
    val host_bw: Long = 10000
    val storage: Long = 100000

    new HostSimple(hosts_ram, host_bw, storage, peList.asJava)
  }

}
