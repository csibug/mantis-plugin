package hudson.plugins.mantis;

import hudson.model.Action;
import java.math.BigInteger;

/**
 * Mantis Register Action 
 * 
 * @author Seiji Sogabe
 */
public class MantisVersionRegisterAction implements Action {

    private MantisSite site;

    private BigInteger versionId;

    public MantisVersionRegisterAction(MantisSite site, BigInteger versionId) {
        this.site = site;
        this.versionId = versionId;
    }

    public BigInteger getVersionId() {
        return versionId;
    }

    public MantisSite getSite() {
        return site;
    }

    @Override
    public String getDisplayName() {
        return "";
    }

    @Override
    public String getIconFileName() {
        return null;
    }

    @Override
    public String getUrlName() {
        return null;
    }
}
