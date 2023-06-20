from django.shortcuts import render
from .models import Prediction

def latest_prediction(request):
    latest_prediction = Prediction.objects.last()
    return render(request, 'predictions/latest_prediction.html', {'prediction': latest_prediction})