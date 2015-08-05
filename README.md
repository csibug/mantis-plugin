# mantis-plugin
Jenkins mantis plugin

Changes to the official plugin:
- Automatic version handling in mantis after successful jenkins build implemented as a post build action.
- Version name can be set in the changeset.
- New version can be created, or the latest existing mantis version can be released and renamed.
- The previously released mantis version can be marked as obsolete.
- Missing version can turn the build process to FAILURE.
