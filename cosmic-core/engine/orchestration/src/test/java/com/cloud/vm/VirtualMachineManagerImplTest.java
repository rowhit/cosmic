// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package com.cloud.vm;

import com.cloud.agent.AgentManager;
import com.cloud.agent.api.*;
import com.cloud.capacity.CapacityManager;
import com.cloud.dao.EntityManager;
import com.cloud.dc.dao.ClusterDao;
import com.cloud.dc.dao.DataCenterDao;
import com.cloud.dc.dao.HostPodDao;
import com.cloud.deploy.DeployDestination;
import com.cloud.deploy.DeploymentPlanner;
import com.cloud.exception.*;
import com.cloud.host.HostVO;
import com.cloud.host.dao.HostDao;
import com.cloud.hypervisor.Hypervisor.HypervisorType;
import com.cloud.hypervisor.HypervisorGuru;
import com.cloud.hypervisor.HypervisorGuruManager;
import com.cloud.offering.ServiceOffering;
import com.cloud.service.ServiceOfferingVO;
import com.cloud.storage.DiskOfferingVO;
import com.cloud.storage.Storage.ProvisioningType;
import com.cloud.storage.StoragePoolHostVO;
import com.cloud.storage.VMTemplateVO;
import com.cloud.storage.VolumeVO;
import com.cloud.storage.dao.DiskOfferingDao;
import com.cloud.storage.dao.StoragePoolHostDao;
import com.cloud.storage.dao.VMTemplateDao;
import com.cloud.storage.dao.VolumeDao;
import com.cloud.user.Account;
import com.cloud.user.AccountVO;
import com.cloud.user.UserVO;
import com.cloud.user.dao.AccountDao;
import com.cloud.user.dao.UserDao;
import com.cloud.utils.Pair;
import com.cloud.utils.exception.CloudRuntimeException;
import com.cloud.vm.VirtualMachine.Event;
import com.cloud.vm.VirtualMachine.PowerState;
import com.cloud.vm.VirtualMachine.State;
import com.cloud.vm.dao.UserVmDao;
import com.cloud.vm.dao.UserVmDetailsDao;
import com.cloud.vm.dao.VMInstanceDao;
import com.cloud.vm.snapshot.VMSnapshotManager;
import junit.framework.Assert;
import org.apache.cloudstack.api.command.user.vm.RestoreVMCmd;
import org.apache.cloudstack.engine.orchestration.service.NetworkOrchestrationService;
import org.apache.cloudstack.engine.orchestration.service.VolumeOrchestrationService;
import org.apache.cloudstack.framework.config.ConfigDepot;
import org.apache.cloudstack.framework.config.dao.ConfigurationDao;
import org.apache.cloudstack.storage.datastore.db.PrimaryDataStoreDao;
import org.apache.cloudstack.storage.datastore.db.StoragePoolVO;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;

public class VirtualMachineManagerImplTest {

    @Spy
    VirtualMachineManagerImpl _vmMgr = new VirtualMachineManagerImpl();
    @Mock
    VolumeOrchestrationService _storageMgr;
    @Mock
    Account _account;
    @Mock
    CapacityManager _capacityMgr;
    @Mock
    AgentManager _agentMgr;
    @Mock
    AccountDao _accountDao;
    @Mock
    ConfigurationDao _configDao;
    @Mock
    HostDao _hostDao;
    @Mock
    UserDao _userDao;
    @Mock
    UserVmDao _vmDao;
    @Mock
    ItWorkDao _workDao;
    @Mock
    VMInstanceDao _vmInstanceDao;
    @Mock
    VMTemplateDao _templateDao;
    @Mock
    VolumeDao _volsDao;
    @Mock
    RestoreVMCmd _restoreVMCmd;
    @Mock
    AccountVO _accountMock;
    @Mock
    UserVO _userMock;
    @Mock
    UserVmVO _vmMock;
    @Mock
    VMInstanceVO _vmInstance;
    @Mock
    HostVO _host;
    @Mock
    VMTemplateVO _templateMock;
    @Mock
    VolumeVO _volumeMock;
    @Mock
    List<VolumeVO> _rootVols;
    @Mock
    ItWorkVO _work;
    @Mock
    HostVO hostVO;
    @Mock
    UserVmDetailVO _vmDetailVO;

    @Mock
    ClusterDao _clusterDao;
    @Mock
    HostPodDao _podDao;
    @Mock
    DataCenterDao _dcDao;
    @Mock
    DiskOfferingDao _diskOfferingDao;
    @Mock
    PrimaryDataStoreDao _storagePoolDao;
    @Mock
    UserVmDetailsDao _vmDetailsDao;
    @Mock
    StoragePoolHostDao _poolHostDao;
    @Mock
    NetworkOrchestrationService _networkMgr;
    @Mock
    HypervisorGuruManager _hvGuruMgr;
    @Mock
    VMSnapshotManager _vmSnapshotMgr;

    // Mock objects for vm migration with storage test.
    @Mock
    DiskOfferingVO _diskOfferingMock;
    @Mock
    StoragePoolVO _srcStoragePoolMock;
    @Mock
    StoragePoolVO _destStoragePoolMock;
    @Mock
    HostVO _srcHostMock;
    @Mock
    HostVO _destHostMock;
    @Mock
    Map<Long, Long> _volumeToPoolMock;
    @Mock
    EntityManager _entityMgr;
    @Mock
    ConfigDepot _configDepot;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        _vmMgr._templateDao = _templateDao;
        _vmMgr._volsDao = _volsDao;
        _vmMgr.volumeMgr = _storageMgr;
        _vmMgr._capacityMgr = _capacityMgr;
        _vmMgr._hostDao = _hostDao;
        _vmMgr._nodeId = 1L;
        _vmMgr._workDao = _workDao;
        _vmMgr._agentMgr = _agentMgr;
        _vmMgr._podDao = _podDao;
        _vmMgr._clusterDao = _clusterDao;
        _vmMgr._dcDao = _dcDao;
        _vmMgr._diskOfferingDao = _diskOfferingDao;
        _vmMgr._storagePoolDao = _storagePoolDao;
        _vmMgr._poolHostDao = _poolHostDao;
        _vmMgr._networkMgr = _networkMgr;
        _vmMgr._hvGuruMgr = _hvGuruMgr;
        _vmMgr._vmSnapshotMgr = _vmSnapshotMgr;
        _vmMgr._vmDao = _vmInstanceDao;
        _vmMgr._uservmDetailsDao = _vmDetailsDao;
        _vmMgr._entityMgr = _entityMgr;
        _vmMgr._configDepot = _configDepot;

        when(_vmMock.getId()).thenReturn(314l);
        when(_vmInstance.getId()).thenReturn(1L);
        when(_vmInstance.getServiceOfferingId()).thenReturn(2L);
        when(_vmInstance.getInstanceName()).thenReturn("myVm");
        when(_vmInstance.getHostId()).thenReturn(2L);
        when(_vmInstance.getType()).thenReturn(VirtualMachine.Type.User);
        when(_host.getId()).thenReturn(1L);
        when(_hostDao.findById(anyLong())).thenReturn(null);
        when(_entityMgr.findById(Matchers.eq(ServiceOffering.class), anyLong())).thenReturn(getSvcoffering(512));
        when(_workDao.persist(_work)).thenReturn(_work);
        when(_workDao.update("1", _work)).thenReturn(true);
        when(_work.getId()).thenReturn("1");
        doNothing().when(_work).setStep(ItWorkVO.Step.Done);
        when(_vmInstanceDao.findByUuid(any(String.class))).thenReturn(_vmMock);
        //doNothing().when(_volsDao).detachVolume(anyLong());
        //when(_work.setStep(ItWorkVO.Step.Done)).thenReturn("1");

    }

    @Test(expected = CloudRuntimeException.class)
    public void testScaleVM1() throws Exception {

        final DeployDestination dest = new DeployDestination(null, null, null, _host);
        final long l = 1L;

        when(_vmInstanceDao.findById(anyLong())).thenReturn(_vmInstance);
        _vmMgr.migrateForScale(_vmInstance.getUuid(), l, dest, l);

    }

    @Test(expected = CloudRuntimeException.class)
    public void testScaleVM2() throws Exception {

        new DeployDestination(null, null, null, _host);
        doReturn(3L).when(_vmInstance).getId();
        when(_vmInstanceDao.findById(anyLong())).thenReturn(_vmInstance);
        final ServiceOfferingVO newServiceOffering = getSvcoffering(512);
        doReturn(1L).when(_vmInstance).getHostId();
        doReturn(hostVO).when(_hostDao).findById(1L);
        doReturn(1L).when(_vmInstance).getDataCenterId();
        doReturn(1L).when(hostVO).getClusterId();
        when(CapacityManager.CpuOverprovisioningFactor.valueIn(1L)).thenReturn(1.0f);
        final ScaleVmCommand reconfigureCmd =
                new ScaleVmCommand("myVmName", newServiceOffering.getCpu(), newServiceOffering.getSpeed(), newServiceOffering.getSpeed(), newServiceOffering.getRamSize(),
                        newServiceOffering.getRamSize(), newServiceOffering.getLimitCpuUse());
        new ScaleVmAnswer(reconfigureCmd, true, "details");
        when(_agentMgr.send(2l, reconfigureCmd)).thenReturn(null);
        _vmMgr.reConfigureVm(_vmInstance.getUuid(), getSvcoffering(256), false);

    }

    @Test(expected = CloudRuntimeException.class)
    public void testScaleVM3() throws Exception {

        /*VirtualMachineProfile profile = new VirtualMachineProfileImpl(vm);

        Long srcHostId = vm.getHostId();
        Long oldSvcOfferingId = vm.getServiceOfferingId();
        if (srcHostId == null) {
            throw new CloudRuntimeException("Unable to scale the vm because it doesn't have a host id");
        }*/

        when(_vmInstance.getHostId()).thenReturn(null);
        when(_vmInstanceDao.findById(anyLong())).thenReturn(_vmInstance);
        when(_vmInstanceDao.findByUuid(any(String.class))).thenReturn(_vmInstance);
        final DeploymentPlanner.ExcludeList excludeHostList = new DeploymentPlanner.ExcludeList();
        _vmMgr.findHostAndMigrate(_vmInstance.getUuid(), 2l, excludeHostList);

    }

    private ServiceOfferingVO getSvcoffering(final int ramSize) {

        final String name = "name";
        final String displayText = "displayText";
        final int cpu = 1;
        //int ramSize = 256;
        final int speed = 128;

        final boolean ha = false;
        final boolean useLocalStorage = false;

        final ServiceOfferingVO serviceOffering =
                new ServiceOfferingVO(name, cpu, ramSize, speed, null, null, ha, displayText, ProvisioningType.THIN, useLocalStorage, false, null, false, null, false);
        return serviceOffering;
    }

    private void initializeMockConfigForMigratingVmWithVolumes() throws OperationTimedoutException, ResourceUnavailableException {

        // Mock the source and destination hosts.
        when(_srcHostMock.getId()).thenReturn(5L);
        when(_destHostMock.getId()).thenReturn(6L);
        when(_hostDao.findById(5L)).thenReturn(_srcHostMock);
        when(_hostDao.findById(6L)).thenReturn(_destHostMock);

        // Mock the vm being migrated.
        when(_vmMock.getId()).thenReturn(1L);
        when(_vmMock.getHypervisorType()).thenReturn(HypervisorType.XenServer);
        when(_vmMock.getState()).thenReturn(State.Running).thenReturn(State.Running).thenReturn(State.Migrating).thenReturn(State.Migrating);
        when(_vmMock.getHostId()).thenReturn(5L);
        when(_vmInstance.getId()).thenReturn(1L);
        when(_vmInstance.getServiceOfferingId()).thenReturn(2L);
        when(_vmInstance.getInstanceName()).thenReturn("myVm");
        when(_vmInstance.getHostId()).thenReturn(5L);
        when(_vmInstance.getType()).thenReturn(VirtualMachine.Type.User);
        when(_vmInstance.getState()).thenReturn(State.Running).thenReturn(State.Running).thenReturn(State.Migrating).thenReturn(State.Migrating);

        // Mock the work item.
        when(_workDao.persist(any(ItWorkVO.class))).thenReturn(_work);
        when(_workDao.update("1", _work)).thenReturn(true);
        when(_work.getId()).thenReturn("1");
        doNothing().when(_work).setStep(ItWorkVO.Step.Done);

        // Mock the vm guru and the user vm object that gets returned.
        _vmMgr._vmGurus = new HashMap<>();
//        UserVmManagerImpl userVmManager = mock(UserVmManagerImpl.class);
//        _vmMgr.registerGuru(VirtualMachine.Type.User, userVmManager);

        // Mock the iteration over all the volumes of an instance.
        final Iterator<VolumeVO> volumeIterator = mock(Iterator.class);
        when(_volsDao.findUsableVolumesForInstance(anyLong())).thenReturn(_rootVols);
        when(_rootVols.iterator()).thenReturn(volumeIterator);
        when(volumeIterator.hasNext()).thenReturn(true, false);
        when(volumeIterator.next()).thenReturn(_volumeMock);

        // Mock the disk offering and pool objects for a volume.
        when(_volumeMock.getDiskOfferingId()).thenReturn(5L);
        when(_volumeMock.getPoolId()).thenReturn(200L);
        when(_volumeMock.getId()).thenReturn(5L);
        when(_diskOfferingDao.findById(anyLong())).thenReturn(_diskOfferingMock);
        when(_storagePoolDao.findById(200L)).thenReturn(_srcStoragePoolMock);
        when(_storagePoolDao.findById(201L)).thenReturn(_destStoragePoolMock);

        // Mock the volume to pool mapping.
        when(_volumeToPoolMock.get(5L)).thenReturn(201L);
        when(_destStoragePoolMock.getId()).thenReturn(201L);
        when(_srcStoragePoolMock.getId()).thenReturn(200L);
        when(_destStoragePoolMock.isLocal()).thenReturn(false);
        when(_diskOfferingMock.getUseLocalStorage()).thenReturn(false);
        when(_poolHostDao.findByPoolHost(anyLong(), anyLong())).thenReturn(mock(StoragePoolHostVO.class));

        // Mock hypervisor guru.
        final HypervisorGuru guruMock = mock(HypervisorGuru.class);
        when(_hvGuruMgr.getGuru(HypervisorType.XenServer)).thenReturn(guruMock);

        when(_srcHostMock.getClusterId()).thenReturn(3L);
        when(_destHostMock.getClusterId()).thenReturn(3L);

        // Mock the commands and answers to the agent.
        final PrepareForMigrationAnswer prepAnswerMock = mock(PrepareForMigrationAnswer.class);
        when(prepAnswerMock.getResult()).thenReturn(true);
        when(_agentMgr.send(anyLong(), isA(PrepareForMigrationCommand.class))).thenReturn(prepAnswerMock);

        final MigrateWithStorageAnswer migAnswerMock = mock(MigrateWithStorageAnswer.class);
        when(migAnswerMock.getResult()).thenReturn(true);
        when(_agentMgr.send(anyLong(), isA(MigrateWithStorageCommand.class))).thenReturn(migAnswerMock);

        final MigrateWithStorageReceiveAnswer migRecAnswerMock = mock(MigrateWithStorageReceiveAnswer.class);
        when(migRecAnswerMock.getResult()).thenReturn(true);
        when(_agentMgr.send(anyLong(), isA(MigrateWithStorageReceiveCommand.class))).thenReturn(migRecAnswerMock);

        final MigrateWithStorageSendAnswer migSendAnswerMock = mock(MigrateWithStorageSendAnswer.class);
        when(migSendAnswerMock.getResult()).thenReturn(true);
        when(_agentMgr.send(anyLong(), isA(MigrateWithStorageSendCommand.class))).thenReturn(migSendAnswerMock);

        final MigrateWithStorageCompleteAnswer migCompleteAnswerMock = mock(MigrateWithStorageCompleteAnswer.class);
        when(migCompleteAnswerMock.getResult()).thenReturn(true);
        when(_agentMgr.send(anyLong(), isA(MigrateWithStorageCompleteCommand.class))).thenReturn(migCompleteAnswerMock);

        final CheckVirtualMachineAnswer checkVmAnswerMock = mock(CheckVirtualMachineAnswer.class);
        when(checkVmAnswerMock.getResult()).thenReturn(true);
        when(checkVmAnswerMock.getState()).thenReturn(PowerState.PowerOn);
        when(_agentMgr.send(anyLong(), isA(CheckVirtualMachineCommand.class))).thenReturn(checkVmAnswerMock);

        // Mock the state transitions of vm.
        final Pair<Long, Long> opaqueMock = new Pair<>(_vmMock.getHostId(), _destHostMock.getId());
        when(_vmSnapshotMgr.hasActiveVMSnapshotTasks(anyLong())).thenReturn(false);
        when(_vmInstanceDao.updateState(State.Running, Event.MigrationRequested, State.Migrating, _vmMock, opaqueMock)).thenReturn(true);
        when(_vmInstanceDao.updateState(State.Migrating, Event.OperationSucceeded, State.Running, _vmMock, opaqueMock)).thenReturn(true);
    }

    // Check migration of a vm with its volumes within a cluster.
    @Test
    public void testMigrateWithVolumeWithinCluster() throws ResourceUnavailableException, ConcurrentOperationException, ManagementServerException,
            VirtualMachineMigrationException, OperationTimedoutException {

        initializeMockConfigForMigratingVmWithVolumes();
        when(_srcHostMock.getClusterId()).thenReturn(3L);
        when(_destHostMock.getClusterId()).thenReturn(3L);

        _vmMgr.migrateWithStorage(_vmInstance.getUuid(), _srcHostMock.getId(), _destHostMock.getId(), _volumeToPoolMock);
    }

    // Check migration of a vm with its volumes across a cluster.
    @Test
    public void testMigrateWithVolumeAcrossCluster() throws ResourceUnavailableException, ConcurrentOperationException, ManagementServerException,
            VirtualMachineMigrationException, OperationTimedoutException {

        initializeMockConfigForMigratingVmWithVolumes();
        when(_srcHostMock.getClusterId()).thenReturn(3L);
        when(_destHostMock.getClusterId()).thenReturn(4L);

        _vmMgr.migrateWithStorage(_vmInstance.getUuid(), _srcHostMock.getId(), _destHostMock.getId(), _volumeToPoolMock);
    }

    // Check migration of a vm fails when src and destination pool are not of same type; that is, one is shared and
    // other is local.
    @Test(expected = CloudRuntimeException.class)
    public void testMigrateWithVolumeFail1() throws ResourceUnavailableException, ConcurrentOperationException, ManagementServerException,
            VirtualMachineMigrationException, OperationTimedoutException {

        initializeMockConfigForMigratingVmWithVolumes();
        when(_srcHostMock.getClusterId()).thenReturn(3L);
        when(_destHostMock.getClusterId()).thenReturn(3L);

        when(_destStoragePoolMock.isLocal()).thenReturn(true);
        when(_diskOfferingMock.getUseLocalStorage()).thenReturn(false);

        _vmMgr.migrateWithStorage(_vmInstance.getUuid(), _srcHostMock.getId(), _destHostMock.getId(), _volumeToPoolMock);
    }

    // Check migration of a vm fails when vm is not in Running state.
    @Test(expected = ConcurrentOperationException.class)
    public void testMigrateWithVolumeFail2() throws ResourceUnavailableException, ConcurrentOperationException, ManagementServerException,
            VirtualMachineMigrationException, OperationTimedoutException {

        initializeMockConfigForMigratingVmWithVolumes();
        when(_srcHostMock.getClusterId()).thenReturn(3L);
        when(_destHostMock.getClusterId()).thenReturn(3L);

        when(_vmMock.getState()).thenReturn(State.Stopped);

        _vmMgr.migrateWithStorage(_vmInstance.getUuid(), _srcHostMock.getId(), _destHostMock.getId(), _volumeToPoolMock);
    }

    @Test
    public void testSendStopWithOkAnswer() throws Exception {
        final VirtualMachineGuru guru = mock(VirtualMachineGuru.class);
        final VirtualMachine vm = mock(VirtualMachine.class);
        final VirtualMachineProfile profile = mock(VirtualMachineProfile.class);
        final StopAnswer answer = new StopAnswer(new StopCommand(vm, false, false), "ok", true);
        when(profile.getVirtualMachine()).thenReturn(vm);
        when(vm.getHostId()).thenReturn(1L);
        when(_agentMgr.send(anyLong(), (Command) any())).thenReturn(answer);

        final boolean actual = _vmMgr.sendStop(guru, profile, false, false);

        Assert.assertTrue(actual);
    }

    @Test
    public void testSendStopWithFailAnswer() throws Exception {
        final VirtualMachineGuru guru = mock(VirtualMachineGuru.class);
        final VirtualMachine vm = mock(VirtualMachine.class);
        final VirtualMachineProfile profile = mock(VirtualMachineProfile.class);
        final StopAnswer answer = new StopAnswer(new StopCommand(vm, false, false), "fail", false);
        when(profile.getVirtualMachine()).thenReturn(vm);
        when(vm.getHostId()).thenReturn(1L);
        when(_agentMgr.send(anyLong(), (Command) any())).thenReturn(answer);

        final boolean actual = _vmMgr.sendStop(guru, profile, false, false);

        Assert.assertFalse(actual);
    }

    @Test
    public void testSendStopWithNullAnswer() throws Exception {
        final VirtualMachineGuru guru = mock(VirtualMachineGuru.class);
        final VirtualMachine vm = mock(VirtualMachine.class);
        final VirtualMachineProfile profile = mock(VirtualMachineProfile.class);
        when(profile.getVirtualMachine()).thenReturn(vm);
        when(vm.getHostId()).thenReturn(1L);
        when(_agentMgr.send(anyLong(), (Command) any())).thenReturn(null);

        final boolean actual = _vmMgr.sendStop(guru, profile, false, false);

        Assert.assertFalse(actual);
    }
}
