package com.andfchat.core.util;

public class Version {

    private final int major;
    private final int minor;
    private final int patch;

    public Version(int major, int minor, int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }

    public Version(String version) {
        String[] brackets = version.split("\\.");

        major = Integer.parseInt(brackets[0]);
        minor = Integer.parseInt(brackets[1]);
        patch = Integer.parseInt(brackets[2]);
    }

    public boolean isLowerThan(Version version) {
        if (major != version.major) {
            return major < version.major;
        }

        if (minor != version.minor) {
            return minor < version.minor;
        }

        if (patch != version.patch) {
            return patch < version.patch;
        }

        return false;
    }

    public boolean isLowerThan(String version) {
        return this.isLowerThan(new Version(version));
    }

    @Override
    public String toString() {
        return major + "." + minor + "." + patch;
    }

}
