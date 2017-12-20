package com.vivid.docker.argument;

/**
 * Created by Phil Madden on 9/10/15.
 */
public class ImageArgumentBuilder<T extends ArgumentBuilder> extends ArgumentBuilder {

    protected ImageArgumentBuilder(String command) {
        super(command);
    }

    public T cpuShares(Integer cpuShares) {
        if(cpuShares != null && cpuShares > 0) {
            argumentListBuilder.addKeyValuePair("--", "cpu-shares", Integer.toString(cpuShares), false);
        }
        return (T) this;
    }

    public T cpuPeriod(Integer cpuPeriod) {
        if(cpuPeriod != null && cpuPeriod > 0) {
            argumentListBuilder.addKeyValuePair("--", "cpu-period", Integer.toString(cpuPeriod), false);
        }
        return (T) this;
    }

    public T cpuQuota(Integer cpuQuota) {
        if(cpuQuota != null && cpuQuota > 0) {
            argumentListBuilder.addKeyValuePair("--", "cpu-quota", Integer.toString(cpuQuota), false);
        }
        return (T) this;
    }

    public T cpus(String cpus) {
        if(isNotEmpty(cpus)) {
            argumentListBuilder.addKeyValuePair("--", "cpuset-cpus", cpus, false);
        }
        return (T) this;
    }

    public T mems(String mems) {
        if(isNotEmpty(mems)) {
            argumentListBuilder.addKeyValuePair("--", "cpuset-mems", mems, false);
        }
        return (T) this;
    }

    public T memorySwap(String memorySwap) {
        if(isNotEmpty(memorySwap)) {
            argumentListBuilder.addKeyValuePair("--", "memory-swap", memorySwap, false);
        }
        return (T) this;
    }

    public T memoryLimit(String memory) {
        if(isNotEmpty(memory)) {
            argumentListBuilder.addKeyValuePair("--", "memory", memory, false);
        }
        return (T) this;
    }

}
