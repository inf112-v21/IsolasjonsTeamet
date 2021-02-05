# INF112 Maven template 
Simple skeleton with libgdx. 

## Project setup for development

1. Clone the project
2. **Importing the project**

   Open the project in IntelliJ. IntelliJ will load the dependencies and other 
   info from the build script. Whenever the build script changes, you will have 
   to reload the build script.
   
   **To reload the build script**
   On the right hand side in IntelliJ, there should be a tab named Gradle. 
   Open this, and find the refresh button. It should be two arrows in a circle.
   
    [!Gradle reload button](docs_images/gradle_reload_button.png)

3. **Setting the formatting style**

   Import and set the formatting style. Go to File -> Settings -> Editor -> Code style. 
   Click on the cog and select Import Scheme -> IntelliJ IDEA code style XML. 
   The file to import is `extra/intellij-java-google-style-adapted.xml`.
   
   [!IntelliJ code style import](docs_images/intellij_code_style_import.png).
   
   Give the scheme a nice and descriptive name, and select it from the scheme dropdown, 
   next to the cog.

    You can now format the code with `Ctrl+Alt+L`.

4. **Setting up IntelliJ with Checkstyle**

   Install the IntelliJ plugin "CheckStyle-IDEA". It's mostly the same as installing 
   the "Code with me" plugin.
   
   Next go File -> Settings -> Tools -> CheckStyle. You need to add our checkstyle config here. 
   Click on the small plus button at the bottom of the "Configuration Files" table. 
   
   A small dialog box should pop up asking for where to find the file, 
   and a nice description for it. You want to use a local Checkstyle file. 
   Browse to `checkstyle.xml` found in the root of this repo.
   
   [!IntelliJ checkstyle add](docs_images/intellij_checkstyle_add.png)

   Then just follow the wizard, and click next and finish a few times.
   It should now be in the table you saw earlier, click on the checkbox to make sure it's active.
   Your code should now show yellow if there are any style issues.

5. **Setting up the precommit script**

   Sometimes you forget to check the formatting before you commit. 
   To prevent this, you can setup a precommit script that is always run before you commit.
   If the script fails, it won't let you commit. I've prepared such a script already that 
   does just this in `extra/pre-commit`. Just copy this file into `.git/hooks/` and you're done.

## Known bugs
