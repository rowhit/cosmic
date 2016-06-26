package org.apache.cloudstack.storage.datastore.provider;

import com.cloud.storage.ScopeType;
import com.cloud.utils.component.ComponentContext;
import org.apache.cloudstack.engine.subsystem.api.storage.DataStoreDriver;
import org.apache.cloudstack.engine.subsystem.api.storage.DataStoreLifeCycle;
import org.apache.cloudstack.engine.subsystem.api.storage.DataStoreProvider;
import org.apache.cloudstack.engine.subsystem.api.storage.HypervisorHostListener;
import org.apache.cloudstack.engine.subsystem.api.storage.ImageStoreProvider;
import org.apache.cloudstack.storage.datastore.driver.S3ImageStoreDriverImpl;
import org.apache.cloudstack.storage.datastore.lifecycle.S3ImageStoreLifeCycleImpl;
import org.apache.cloudstack.storage.image.ImageStoreDriver;
import org.apache.cloudstack.storage.image.datastore.ImageStoreHelper;
import org.apache.cloudstack.storage.image.datastore.ImageStoreProviderManager;
import org.apache.cloudstack.storage.image.store.lifecycle.ImageStoreLifeCycle;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

@Component
public class S3ImageStoreProviderImpl implements ImageStoreProvider {

    private final String providerName = DataStoreProvider.S3_IMAGE;
    protected ImageStoreLifeCycle lifeCycle;
    protected ImageStoreDriver driver;
    @Inject
    ImageStoreProviderManager storeMgr;
    @Inject
    ImageStoreHelper helper;

    @Override
    public DataStoreLifeCycle getDataStoreLifeCycle() {
        return lifeCycle;
    }

    @Override
    public DataStoreDriver getDataStoreDriver() {
        return this.driver;
    }

    @Override
    public HypervisorHostListener getHostListener() {
        return null;
    }

    @Override
    public String getName() {
        return this.providerName;
    }

    @Override
    public boolean configure(final Map<String, Object> params) {
        lifeCycle = ComponentContext.inject(S3ImageStoreLifeCycleImpl.class);
        driver = ComponentContext.inject(S3ImageStoreDriverImpl.class);
        storeMgr.registerDriver(this.getName(), driver);
        return true;
    }

    @Override
    public Set<DataStoreProviderType> getTypes() {
        final Set<DataStoreProviderType> types = new HashSet<>();
        types.add(DataStoreProviderType.IMAGE);
        return types;
    }

    @Override
    public boolean isScopeSupported(final ScopeType scope) {
        if (scope == ScopeType.REGION) {
            return true;
        }
        return false;
    }

    @Override
    public boolean needDownloadSysTemplate() {
        return true;
    }
}
