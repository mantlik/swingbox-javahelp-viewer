Swingbox-javahelp-viewer plugin can be installed into a Netbeans RCP application. It replaces default Javahelp content viewer by a content viewer based on  [SwingBox BrowserPane](http://cssbox.sourceforge.net/swingbox) component.

When help window is first open in a Netbeans RCP application, a content viewer component based on Swingbox BrowserPane is created and replaces original Javahelp content viewer. All following Javahelp requests are handled by the new component.

# Features #

  * Swingbox viewer is based on [CSSBox](http://cssbox.sourceforge.net) rendering engine.It serves more advanced rendering capabilities than built-in Javahelp viewer

  * Support of HTML v.4.0 and CSS v.2.1

  * In the contrary to the Javahelp system, CSSBox package is under active development, future enhancements could be expected.

# Screenshot #

[![](https://github.com/mantlik/swingbox-javahelp-viewer/blob/wiki/screenshot_swingbox_javahelp_small.png?raw=true)](https://github.com/mantlik/swingbox-javahelp-viewer/wiki/ScreenshotPage)

Netbeans IDE help with swingbox-javahelp-viewer plugin installed. Presentation of highlighted search results.

# Usage #

**Users** can install the plugin using RCP application plugin manager. Open plugin manager selecting Tools-Plugins from the application menu. At the Download tab click Add plugin button and locate downloaded .nbm file. Finally press the Install button and follow installation wizard.

Stable version of the plugin is available in the Netbeans Plugin Portal update center. You can check the Plugin Portal checkbox at the Settings tab and the plugin will appear in the list of Available plugins.

**Developers** can integrate the plugin into their application by defining dependency of an application's module containing a helpset on the plugin module.

E.g. Netbeans Maven application module can easily include the plugin this way:

  * Include swingbox-javahelp-viewer distribution repository into your main project repositories list in it's pom.xml:
```
    <repositories>
        <repository>
            <id>swingbox-javahelp-viewer</id>
            <url>http://host.mantlik.cz/nexus/content/groups/public</url>
        </repository>
    </repositories>
```
  * In any module containing a help set include dependecy in the module's pom.xml:
```
        <dependency>
            <groupId>org.mantlik</groupId>
            <artifactId>swingbox-javahelp-viewer</artifactId>
            <version>1.1.1</version>
        </dependency>
```

**Important note**

Application has to provide XercesImpl 2.8.0 or newer. Plugin will not work if xerces is not present in the application context classpath. When your application or any dependent module do not need xerces, simply include dependency on it, e.g.:

```
        <dependency>
            <groupId>xerces</groupId>
            <artifactId>xercesImpl</artifactId>
            <version>2.9.1</version>
        </dependency>
```

[Release history](https://github.com/mantlik/swingbox-javahelp-viewer/wiki/ReleaseHistory)

# Limitations #

  * Reference of type `<a href="mailto:...">` is not supported.

  * When Help window is first opened, Javahelp component displays content for a while until Swingbox component is created. This can temporarily distort user experience. Any consequent help request is opened in the Swingbox component without further delay.

# Feedback #

Any feedback is welcome at the [Issue Tracking page](https://github.com/mantlik/swingbox-javahelp-viewer/issues).

Developers are encouraged to checkout or clone the [Git Repository](https://github.com/mantlik/swingbox-javahelp-viewer) and submit their patches/improvements.

# References #

Swingbox project: http://cssbox.sourceforge.net/swingbox

This project is based on ideas presented at the following resources:

http://weblogs.java.net/blog/brinkley/archive/2004/11/javahelp_v20_02.html describes instrutions how to implement Javahelp custom content viewer and

http://www.javafaq.nu/java-bookpage-36-6.html presents idea how to catch and change JHelpContentViewer using WindowsFocusListener.
