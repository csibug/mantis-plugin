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
import hudson.plugins.mantis.model.MantisProject;
import hudson.plugins.mantis.model.MantisProjectVersion;
import hudson.scm.ChangeLogSet.Entry;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigInteger;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.kohsuke.stapler.DataBoundConstructor;

/**
 *
 * @author csibug
 */
public final class MantisVersionRegister extends Recorder {
    
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
        
        MantisProjectVersion version = createVersion(build, listener);
        if (version == null) {
            Utility.log(logger, "skipping generation of mantis version ...");
            return true;
        }
        
        MantisProjectVersion cv;
        
        try {
            if (NEW.equalsIgnoreCase(this.versioningType)) {
                cv = site.createProjectVersion(version);
                Utility.log(logger, "created a version: " + cv.toString());
            } else {
                cv = site.getLatestProjectVersion(version);
                if (cv == null) {
                    Utility.log(logger, "working mantis version to be updated not found.");
                    return true;
                }
                cv.setDateOrder(version.getDateOrder());
                cv.setReleased(true);
                cv.setVersion(version.getVersion());
                boolean resp = site.updateProjectVersion(cv);
                Utility.log(logger, "update of version " + cv + " done with result: " + resp);
            }
        } catch (MantisHandlingException e) {
            Utility.log(logger, e.toString());
            build.setResult(Result.FAILURE);
            return true;
        }
        
        build.getActions().add(new MantisVersionRegisterAction(site, cv.getId()));
        
        return true;
    }
    
    private String findVersionFromSCM(final AbstractBuild<?, ?> build, BuildListener listener) {
        final PrintStream logger = listener.getLogger();
        MantisProjectProperty mpp = MantisProjectProperty.get(build);
        Pattern p = mpp.getVersionRegexpPattern();
        for (final Entry change : build.getChangeSet()) {
            Matcher matcher = p.matcher(change.getMsg());
            Utility.log(logger, "change msg: " + change.getMsg());
            Utility.log(logger, "version pattern:  " + p.toString());
            while (matcher.find()) {
                String version = matcher.group(1);
                if (version != null && version.length() >0)
                    return version;
            }
        }
        
        return null;
    }
    
    private MantisProjectVersion createVersion(AbstractBuild<?, ?> build, BuildListener listener) 
            throws IOException, InterruptedException {
        MantisProjectProperty mpp = MantisProjectProperty.get(build);
        int projectId = mpp.getProjectId();
        if (projectId == MantisProject.NONE) {
            Utility.log(listener.getLogger(), "Project is not selected.");
            return null;
        }
        
        MantisProject project = new MantisProject(projectId);
        String findVersionFromSCM = findVersionFromSCM(build, listener);
        if (findVersionFromSCM == null) {
            Utility.log(listener.getLogger(), "mantis version not found in scm changeset");
            return null;
        }
        return new MantisProjectVersion(BigInteger.valueOf(projectId), null, 
                findVersionFromSCM, "jenkins version", true);
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
