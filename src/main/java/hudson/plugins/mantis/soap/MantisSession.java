package hudson.plugins.mantis.soap;

import hudson.plugins.mantis.MantisHandlingException;
import hudson.plugins.mantis.model.MantisCategory;
import hudson.plugins.mantis.model.MantisIssue;
import hudson.plugins.mantis.model.MantisNote;
import hudson.plugins.mantis.model.MantisProject;
import hudson.plugins.mantis.model.MantisProjectVersion;
import java.math.BigInteger;
import java.util.List;

/**
 *
 * @author sogabe
 */
public interface MantisSession {

    void addNote(final int id, final MantisNote note) throws MantisHandlingException;

    String getVersion() throws MantisHandlingException;

    MantisIssue getIssue(final int id) throws MantisHandlingException;

    List<MantisProject> getProjects() throws MantisHandlingException;
    
    List<MantisCategory> getCategories(int projectId) throws MantisHandlingException;
    
    int addIssue(MantisIssue issue) throws MantisHandlingException;
    
    MantisProjectVersion addProjectVersion(MantisProjectVersion version) throws MantisHandlingException;
    
    boolean updateProjectVersion(MantisProjectVersion version) throws MantisHandlingException;
    
    List<MantisProjectVersion> getProjectVersions(BigInteger projectId) throws MantisHandlingException;
}
