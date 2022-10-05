"""NeuroscienceResearchDevTeam URL Configuration

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/3.2/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  path('', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  path('', Home.as_view(), name='home')
Including another URLconf
    1. Import the include() function: from django.urls import include, path
    2. Add a URL to urlpatterns:  path('blog/', include('blog.urls'))
"""
from django.contrib import admin
from django.urls import path
from django.conf import settings
from django.conf.urls.static import static

import slopes.views as views
from django.urls import include, path

urlpatterns = [
    path('', views.index, name='index'),
    path('slopes/', include('slopes.urls')),
    path('admin/', admin.site.urls),
    path('about/',views.about, name='about'),
    path('survey/', views.survey, name='survey'),
    path('take_survey/', views.take_survey, name='take_survey'),
    path('safety_info/', views.safety_info, name='safety_info'),
    path('survey_welcome/', views.survey_welcome, name='survey_welcome'),
    path('survey_thanks/', views.survey_thanks, name='survey_thanks'),
    path('exit_survey/', views.exit_survey, name='exit_survey'),
    path('<int:question_id>/', views.survey, name='survey'),
    path('survey/<int:question_id>/', views.survey, name='survey'),
    path('csv/', views.csv_file, name='csv_file'),
    path('csv_emails/', views.csv_emails, name='csv_emails'),
    path('csv_user_results/', views.csv_user_results, name='csv_user_results'),
    path('csv_demographics/', views.csv_demographics, name='csv_demographics'),

]


