import torch
import torch.nn as nn
import numpy as np
import cv2
from torchvision import models
import ctypes

class EmoNet(nn.Module):

    def __init__(self):
        super(EmoNet, self).__init__()
        self.reset()
        self.features = nn.Sequential(
            nn.Conv2d(1, 16, 3, padding=1),
            nn.BatchNorm2d(16),
            nn.MaxPool2d(2, 2),
            nn.ReLU(),
            nn.Dropout(0.1),

            nn.Conv2d(16, 32, 3, padding=1),
            nn.BatchNorm2d(32),
            nn.MaxPool2d(2, 2),
            nn.ReLU(),
            nn.Dropout(0.2),
            
            nn.Conv2d(32, 64, 3, padding=1),
            nn.BatchNorm2d(64),
            nn.MaxPool2d(2, 2),
            nn.ReLU(),
            nn.Dropout(0.3),
            
            nn.Conv2d(64, 128, 3, padding=1),
            nn.BatchNorm2d(128),
            nn.MaxPool2d(2, 2),
            nn.ReLU(),
            nn.Dropout(0.4)
        )
        self.classifier = nn.Sequential(
            nn.Linear(128*3*3, 200),
            nn.ReLU(),
            nn.Dropout(0.5),
            nn.Linear(200, 7)
        )

    def forward(self, x):
        x = self.features(x)
        x = x.view(-1, self.num_flat_features(x))
        x = self.classifier(x)
        return x
    
    def num_flat_features(self, x):
        n_feats = 1
        for s in x.size()[1:]:
            n_feats *= s

        return n_feats
    
    def reset(self):
        self.train_loss_history = []
        self.train_acc_history = []
        self.val_loss_history = []
        self.val_acc_history = []

    def print_params(self):
        total_params = sum(param.numel() for param in self.parameters())
        trainable = sum(param.numel() for param in self.parameters() if param.requires_grad == True)
        print('Total params: {}\nTrainable params: {}'.format(total_params, trainable))

model = EmoNet()
print(model)

face_cascade = cv2.CascadeClassifier('C:/Users/h80054936/AppData/Local/Continuum/anaconda3/Lib/site-packages/cv2/data/haarcascade_frontalface_default.xml')

saved_model = models.VGG('VGG19')
saved_model = torch.load('C:/Users/h80054936/Documents/intellibuddy/src/Saved_Models/model1.pt', map_location=torch.device('cpu'))
# saved_model.eval()
print(saved_model)

classes = ('Angry', 'Disgust', 'Fear', 'Happy', 'Sad', 'Surprise', 'Neutral')
cap = cv2.VideoCapture(0)

count = 0
while cap.isOpened():
    ret, frame = cap.read()
    # frame = frame.resize(200, 300)
    gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
    faces = face_cascade.detectMultiScale(gray, 1.3, 5)
    for (x,y,w,h) in faces:
        cv2.rectangle(frame,(x,y),(x+w,y+h),(255,0,0),2)
        face_gray = gray[y:y+h, x:x+w]
        sample = cv2.resize(face_gray, (48, 48))
        sample = sample.astype('float32')/255.
        sample = np.asarray(sample).reshape(1,48,48)
        sample = torch.from_numpy(sample).unsqueeze(0)
        output = saved_model(sample)
        output = output.squeeze(0).detach().numpy()
        output = np.exp(output)/sum(np.exp(output))
        stress_level = sum(output[0:2]) + output[4] + output[5] + output[6]
        label = np.float('{:.3f}'.format(stress_level))
        label *= 100
        
        count += 1

        with open('C:/Users/h80054936/Documents/intellibuddy/src/stress.txt', 'w') as f:
            f.writelines(str(int(label))+'\n')
                    
        cv2.putText(frame, str(label), (x-10, y-20), cv2.FONT_HERSHEY_SIMPLEX, 1.2, (0,0,255), 1, cv2.LINE_AA)
    cv2.imshow("Frame", frame)
    
    # notepad_handle = ctypes.windll.user32.FindWindowW(None, "Frame")
    # ctypes.windll.user32.ShowWindow(notepad_handle, 6)
    key = cv2.waitKey(1) & 0xFF
    
    if key == ord('q'):
        break
        
cap.release()
cv2.destroyAllWindows()
cv2.waitKey(1)