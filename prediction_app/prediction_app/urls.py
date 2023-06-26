from django.contrib import admin
from django.urls import path, include

urlpatterns = [
    path('admin/', admin.site.urls),
    path('predictions/', include('predictions.urls')),
    path('api-auth', include('rest_framework.urls')),
    
]