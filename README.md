# Chill Me Out!
Coding is fun, but can be stressful sometimes. How do you manage that stress? What if your IDE could cheer you up while you code? Pretty cool, right! That’s where **Chill Me Out!** comes in. Get real-time insights on your stress levels in IntelliJ IDE.

<p align="center">
  <img src="https://github.com/user-attachments/assets/8944e3fb-88a6-419f-b372-5f5f3e7fd3a7">
</p>

This repository is a result of a project submitted in hackaTUM 2019 (JetBrains track) at the Technical University of Munich. We developed an IntelliJ plugin that calls a Python script,  consisting of a trained deep learning classifier (`model.py`) that predicts the stress level (0-100) of the user. The name of the plugin is **Intellibuddy** while the project itself was named **chill-me-out**. Of course, this project was all for fun and there is no scientific idea behind this 😉 

The stress level is divided into ranges and these ranges correspond to the emotion of the person writing the code. For example, if the person is happy 😄, the stress level could be mapped to the range 0-20 while if the person is angry 😠, the stress level could be well above 80. This is done in real-time through OpenCV's `VideoCapture`. The stress value is written to a text file (`stress.txt`) that is read concurrently by the Java plugin app. Depending on the stress level, a Joke API is called, sending back random programming jokes from the Internet. The joke along with a personalized message is shown in a tooltip in the lower right corner.  

Find a demo of the project [here](https://www.youtube.com/watch?v=WXgcFlkxijM&feature=youtu.be)!
