import pickle
import numpy as nm 
loaded_model = pickle.load(open('C:/Users/nican/Desktop/finalized_model.sav', 'rb'))
prova= nm.array([[-68,-70,-75,-80,-82]])
print(loaded_model.predict(prova))