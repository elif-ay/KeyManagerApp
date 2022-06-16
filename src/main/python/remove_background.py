import requests
from os.path import dirname, join

def main():
    response = requests.post(
        'https://api.remove.bg/v1.0/removebg',
        data={
        'image_url': 'https://firebasestorage.googleapis.com/v0/b/keymanager-2cf3e.appspot.com/o/CapturedPhotos%2F116064?alt=media&token=2c5126c0-7055-4a00-9cf5-060d867714af',
        'size': 'auto'},
        headers={'X-Api-Key': 'rWTUhVb46nCiQEG6NHuDsGGP'},
    )
    if response.status_code == requests.codes.ok:
        with open('/data/user/0/com.gtu.keymanager/app_app_images/myImageNew.png', 'wb') as out:
            out.write(response.content)
    else:
        print("Error:", response.status_code, response.text)

    return 61