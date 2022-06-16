import pyrebase
def main():
    config = {
        "apiKey": "AIzaSyCa7ihqeHF52aRn7EU9hPR00Ivu80tj0PE",
        "authDomain": "keymanager-2cf3e.firebaseapp.com",
        "projectId": "keymanager-2cf3e",
        "storageBucket": "keymanager-2cf3e.appspot.com",
        "messagingSenderId": "98300148048",
        "appId": "1:98300148048:web:a14e63f2294de25ab39455",
        "measurementId": "G-26JMGF4R4T",
        "serviceAccount" : "key.json",
        "databaseURL": "https://keymanager-2cf3e-default-rtdb.firebaseio.com/"
    }

    firebase = pyrebase.initialize_app(config)
    storage = firebase.storage()
 #   storage.child("CapturedPhotos/imageup.jpeg").put("image.jpeg") # for upload

    storage.download("CapturedPhotos/imageup.jpeg", "imagedown.jpeg") # for download

main()