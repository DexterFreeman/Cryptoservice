from django.urls import path
from .views import latest_prediction, create_prediction

urlpatterns = [
    path('latest-prediction/', latest_prediction, name='latest_prediction'),
    path('create-prediction/', create_prediction, name='create_prediction')
]