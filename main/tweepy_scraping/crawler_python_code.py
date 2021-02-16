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

def download(link, filelocation):
    try:
        r = requests.get(link, stream=True)
        if not os.path.exists(os.path.dirname(filelocation)):
            try:
                os.makedirs(os.path.dirname(filelocation))
            except OSError as exc: # Guard against race condition
                pass
        with open(filelocation, 'wb') as f:
            for chunk in r.iter_content(1024):
                if chunk:
                    f.write(chunk)
    except Exception as e:
        print(e)
class CustomStreamListener(tweepy.StreamListener):
    def on_status(self, status):
        urls = [link['expanded_url'] for link in status.entities['urls']]
        for url in urls:
            filename = url
            filename = filename.replace('https://', '')
            filename = filename.replace('http://', '')
            filename = filename.replace('/', '_')
            filelocation = os.path.join('downloads', str(status.id), filename)
            download_thread = threading.Thread(target=download, args=(url, filelocation))
            download_thread.start()
        with open('../output.csv', 'a') as f:
            writer = csv.writer(f)
            writer.writerow([
                status.id,
                status.author.screen_name,
                status.created_at,
                status.text,
                status.place,
                ['#{}'.format(hashtag['text']) for hashtag in status.entities['hashtags']],
                urls
            ])
            if f.tell() > MAX_SIZE:
                return False
    def on_error(self, status_code):
        print('error {}'.format(status_code))
streamingAPI = tweepy.streaming.Stream(auth, CustomStreamListener())
streamingAPI.filter(track=['Biden', 'President', 'Kamala Harris' 'United States'], is_async=True)
