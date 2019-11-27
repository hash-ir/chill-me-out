# Chill Me Out!
Coding is fun! But sometimes, stressful. How do you manage that stress? What if your IDE could cheer you up while you code? Pretty cool, right! That’s where **Chill Me Out!** dives in. Get real-time insights on your stress levels in IntelliJ IDE.

<p align="center">
  <img width="460" height="300" src="https://www.narayanahealth.org/blog/wp-content/uploads/2019/06/shutterstock_777016792-Converted-01.jpg">
</p>

This project was implemented and submitted in hackaTUM 2019 at Technical University of Munich under the JetBrains track. We developed an IntelliJ plugin which invokes a Python script. The script consists of a trained Deep Learning Model (`model.py`) which predicts the stress level (0-100) of the user. This is done in real-time through VideoCapture from OpenCV. The stress value is written to a text file (`stress.txt`) which is read simulataneously by the plugin app. Depending on the stress level, a Joke API is invoked which sends back random programming jokes from the Internet. Currently, the stress level is thresholded at 65. The final feedback is presented as a message in a tooltip in the lower right corner.  

### YouTube Link
Find a demo of the project [here](https://www.youtube.com/watch?v=WXgcFlkxijM&feature=youtu.be).
