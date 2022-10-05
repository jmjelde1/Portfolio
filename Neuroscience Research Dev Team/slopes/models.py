from django.db import models
from django.db.models import Model
import datetime
#from multiselectfield import MultiSelectField


#classes that map to tables in the DB. Models have fields that become columns
#django auto generates a PK for you. Autoincrement synthetic key
#pk may not be the best to use in urls for security purposes. Another option is to usa a UUID(32 char hex code) for Pks.
#text field long string - paragraphs
# Create your models here.
#Users
#Permission
#Groups
#Authentication
#object level permissions
#questions
class Questions(models.Model):
    VIEW_ANGLES = (
        ('Top Down', 'Top Down'),
        ('Cross Sectional', 'Cross Sectional'),
        ('Bottom Up', "Bottom up"),
        ('Side View ', 'Side View'),
    )
    image_name = models.CharField(max_length=200, default='What is the slope?')
    location = models.CharField(max_length=200, default='Drag the slider to answer the question')
    image = models.ImageField(default=None, upload_to='slopes/static/home/Survey Images')
    actual = models.IntegerField(blank = True, null = True)
    view_angle = models.CharField(max_length = 20, choices = VIEW_ANGLES, blank = True, null = True)


    def image_url(self):
        url = self.image.__str__()
        url = url.replace('slopes/static/', '')

        return url




    def __str__(self):
        return self.question_text


class Survey(models.Model):
    questions = models.ManyToManyField(Questions, through='QuestionOnSurvey')

class ExitSurvey(models.Model):
    # location
    # gedner\
    # email
    GENDER_CHOICES = (
        ('Male', 'Male'),
        ('Female', 'Female'),
        ('Non-binary', "Non-Binary"),
        ('Other', 'Other'),
    )

    ACTIVITY_CHOICES = (
        ('Walking/Hiking', 'Walking/Hiking'),
        ('Skiing', 'Skiing'),
        ('Snowboarding', "Snowboarding"),
        ('Snowmobiling or other motorized', 'Snowmobiling or other motorized'),
        ('Other', 'Other'),

    )


    age = models.IntegerField(blank = True, null = True)
    gender = models.CharField(max_length=20, choices=GENDER_CHOICES, null=True)
    zipcode = models.IntegerField(blank = True, null = True)
    experience = models.IntegerField(blank = True, null = True)
    averageDays = models.IntegerField(blank = True, null = True)
    activity = models.CharField(choices=ACTIVITY_CHOICES, default = 'EMPTY', max_length = 50)
    #email = models.EmailField(max_length=254, null=True)
    #location = models.CharField(max_length=200, null=True)


class QuestionOnSurvey(models.Model):
    survey = models.ForeignKey(to=Survey, null=False, on_delete=models.CASCADE)
    question = models.ForeignKey(to=Questions, null=False, on_delete=models.CASCADE)
    number = models.IntegerField()
    response = models.IntegerField(null=True, blank=True)
    #actual = models.ForeignKey(to=Questions, null=True, on_delete=models.CASCADE)


class Email(models.Model):
    email = models.EmailField(max_length=254, null=True, blank=True)


# Table of all users and answers to each question
class User(models.Model):
    survey = models.ForeignKey(to=Survey, null=False, on_delete=models.CASCADE)
    exitSurvey = models.ForeignKey(to=ExitSurvey, null=True, on_delete=models.CASCADE)
    ip_addy = models.CharField(max_length=200, null=True)


class response(models.Model):
    slopeGuess = models.IntegerField()


    #class quizSession():

# def create_superuser_if_necessary():
#     # Set the name and initial password you want the superuser to have here.
#     # AFTER THIS SUPERUSER IS CREATED ON HEROKU, YOU ***MUST*** IMMEDIATELY CHANGE ITS PASSWORD
#     # THROUGH THE ADMIN INTERFACE. (This password is stored in cleartext in a GitHub repository,
#     # so it is not acceptable to use it when there is actual client data!)
#     SUPERUSER_NAME = 'admin'
#     SUPERUSER_PASSWORD = 'fake_password'
#
#     from django.contrib.auth.models import User
#
#     if not User.objects.filter(username=SUPERUSER_NAME).exists():
#         superuser = User(
#             username=SUPERUSER_NAME,
#             is_superuser=True,
#             is_staff=True
#         )
#
#         superuser.save()
#         superuser.set_password(SUPERUSER_PASSWORD)
#         superuser.save()
#
#
# # Once the superuser has been created on Heroku, you can comment out this line if you wish
# create_superuser_if_necessary()