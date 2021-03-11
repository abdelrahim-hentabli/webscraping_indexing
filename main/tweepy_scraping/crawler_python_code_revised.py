import csv
import os
import threading
import requests
import tweepy
from dotenv import load_dotenv

load_dotenv()
MAX_SIZE = int(os.getenv('MAX_SIZE'))
CONSUMER_KEY = os.getenv('CONSUMER_KEY')
CONSUMER_SECRET = os.getenv('CONSUMER_SECRET')
ACCESS_TOKEN = os.getenv('ACCESS_TOKEN')
ACCESS_SECRET = os.getenv('ACCESS_SECRET')
auth = tweepy.OAuthHandler(CONSUMER_KEY, CONSUMER_SECRET)
auth.set_access_token(ACCESS_TOKEN, ACCESS_SECRET)

class CustomStreamListener(tweepy.StreamListener):
    def on_status(self, status):
        with open(os.path.join(os.getcwd(),'..','tweets_new.csv'), 'a') as f:
            if hasattr(status, "retweeted_status"):
                try:
                    writer = csv.writer(f)
                    writer.writerow([
                        status.id,
                        status.author.screen_name,
                        status.created_at,
                        status.retweeted_status.extended_tweet["full_text"],
                        status.place,
                        status.retweet_count,
                        ['#{}'.format(hashtag['text']) for hashtag in status.entities['hashtags']]
                    ])
                except AttributeError:
                    writer = csv.writer(f)
                    writer.writerow([
                        status.id,
                        status.author.screen_name,
                        status.created_at,
                        status.retweeted_status.text,
                        status.place,
                        status.retweet_count,
                        ['#{}'.format(hashtag['text']) for hashtag in status.entities['hashtags']]
                    ])
            else:
                try: 
                    writer = csv.writer(f)
                    writer.writerow([
                        status.id,
                        status.author.screen_name,
                        status.created_at,
                        status.full_text,
                        status.place,
                        status.retweet_count,
                        ['#{}'.format(hashtag['text']) for hashtag in status.entities['hashtags']]
                    ])
                except AttributeError:
                    writer.writerow([
                        status.id,
                        status.author.screen_name,
                        status.created_at,
                        status.text,
                        status.place,
                        status.retweet_count,
                        ['#{}'.format(hashtag['text']) for hashtag in status.entities['hashtags']]
                    ])
            if(f.tell()//(1024**3) > 0):
                print("%3.2f GB         " %(f.tell()/(1024**3)), end="\r")
            elif(f.tell()//(1024**2) > 0):
                print("%3.2f MB         " %(f.tell()/(1024**2)), end="\r")
            elif(f.tell()/1024 > 0):
                print("%3.2f KB         " %(f.tell()/1024), end="\r")
            else:
                print("%3.2f Bytes      " %(f.tell()), end="\r")
            if f.tell() > MAX_SIZE:
                return False
    def on_error(self, status_code):
        print('error {}'.format(status_code))
streamingAPI = tweepy.streaming.Stream(auth, CustomStreamListener(), tweet_mode= 'extended', allow_retweets=False)
streamingAPI.filter(track=['Biden', 'President', 'Harris', 'Trump', 'Pence', 'Inauguration', 'Impeachment', 'Ted Cruz', 'Mitch McConnell', 'Congress', 'Senate', 'House of Representatives'], is_async=True)
