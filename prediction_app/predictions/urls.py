from django.urls import path
from .views import latest_prediction

urlpatterns = [
    path('latest-prediction/', latest_prediction, name='latest_prediction'),
]