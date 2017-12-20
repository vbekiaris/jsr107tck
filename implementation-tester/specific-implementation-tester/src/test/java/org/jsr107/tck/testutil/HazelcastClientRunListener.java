package org.jsr107.tck.testutil;

import org.junit.runner.Description;
import org.junit.runner.notification.RunListener;
import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;

public class HazelcastClientRunListener
        extends RunListener {

    public HazelcastClientRunListener() {
    }

    static {
        Config config = new Config();
        config.getNetworkConfig().getJoin().getAwsConfig().setEnabled(false);
        config.getNetworkConfig().getJoin().getTcpIpConfig().setEnabled(false);
        config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
        Hazelcast.newHazelcastInstance(config);
    }

    @Override
    public void testStarted(Description description) {
    }
}
