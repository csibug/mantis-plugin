/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hudson.plugins.mantis;

import hudson.Extension;
import hudson.Launcher;
import hudson.Util;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.plugins.mantis.model.MantisCategory;
import hudson.plugins.mantis.model.MantisIssue;
import hudson.plugins.mantis.model.MantisProject;
import hudson.plugins.mantis.model.MantisProjectVersion;
import hudson.plugins.mantis.model.MantisViewState;
import hudson.plugins.mantis.scripts.JellyScriptContent;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigInteger;
import java.util.logging.Logger;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 *
 * @author csibug
 */
public class MantisVersionRegister extends Recorder {
    
    private String versioningType;
    
    private boolean obsoletePrev;
    
    public static final String NEW = "new";
    
    public static final String RENAMELATEST = "renameLatest";
    
    @DataBoundConstructor
    public MantisVersionRegister(String versioningType, boolean obsoletePrev) {
        this.versioningType = Util.fixEmptyAndTrim(versioningType);
        this.obsoletePrev = obsoletePrev;
    }
    
    public String getVersioningType() {
        return versioningType;
    }

    public boolean isObsoletePrev() {
        return obsoletePrev;
    }
    
    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }
    
    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener)
            throws InterruptedException, IOException {
        
        final PrintStream logger = listener.getLogger();

        
        MantisSite site = MantisSite.get(build.getProject());
        if (site == null) {
            Utility.log(logger, Messages.MantisIssueRegister_NoMantisSite());
            build.setResult(Result.FAILURE);
            return true;
        }
        
        MantisProjectProperty mpp = MantisProjectProperty.get(build);
        
        /*
        MantisProjectVersion version = new MantisProjectVersion(null, 
                BigInteger.valueOf(mpp.getProjectId()), mpp.g, "jenkins generated", true);
        try {
            site.createProjectVersion(version);
        } catch (MantisHandlingException e) {
            Utility.log(logger, e.toString());
            build.setResult(Result.FAILURE);
            return true;
        }
        */
        
        return true;
    }
    
    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {
        
        public DescriptorImpl() {
            super(MantisVersionRegister.class);
        }
        
        @Override
        public String getDisplayName() {
            return Messages.MantisVersionRegister_DisplayName();
        }
        
        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }
    }
    
    private static final Logger LOGGER = Logger.getLogger(MantisVersionRegister.class.getName());
}
