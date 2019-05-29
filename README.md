# sof-ecl-plugins

Eclipse Plugins for SOF

## How to build and install the plug-ins

Download and install preferred version (>2019-09) of the Eclipse CDT.

Install the following GEF plug-ins required by graph related features:
- org.eclipse.gef.mvc.fx (>= 5.0.2)
- org.eclipse.gef.mvc.fx.ui (>= 5.0.2)
- org.eclipse.gef.common (>= 5.0.0)
- org.eclipse.gef.zest.fx (>= 5.0.1)
- org.eclipse.gef.zest.fx.ui (>= 5.0.2)

(Use *Help > Install new software...*).

TODO: provide binary release?
Build the plug-ins (see *How to begin the development*).

Copy SOF plug-in jar-s to the plugins subfolder of your CDT installation.

## How to Begin Development

NOTE: This manual is based on Eclipse 2018-09 as the development platform.
Please make necessary replacements if a newer version is used.

### Requirements
- Eclipse >= 2018-09 for committers.
- JavaSE-1.8.
- e(fx)clipse  - IDE - PDE

1. Download and install Eclipse for Committers (>= 2018-09).

2. Create a new workspace in the eclipse plug-ins root directory.
   `tools/ecl-plugins` and import the projects.

3. Open `gef-integration.target/gef-integration.target` **as a text file** and
   edit the repository path according to the eclipse version you are running.
   Now re-open that file using *default* editor and wait until it resolves
   (the progress is displayed in the lower right corner, it may take a while
   but it is a one time operation). Once the operation is complete, click on
   the *Set as Target Platform* link in the upper right corner of the editor
   window.

4. Now you may create and launch the configuration to test the plug-ins.
   Open *Run > Run Configurations...*, select *Eclipse Application* and
   click on *New launch configuration*.
   The *Execution environment* should point to the **JavaSE-1.8**. Switch to
   the *Arguments* tab and add `-Dosgi.framework.extensions=org.eclipse.fx.osgi`
   to the *VM arguments*.

Now you are ready to run a new instance of Eclipse that includes the plug-ins.
Click the *Run* button.
