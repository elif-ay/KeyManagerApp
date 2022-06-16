import cv2
import numpy as np
from PIL import Image
import base64
import io


def main(data1, data2):
    dec_data1 = base64.b64decode(data1)
    np_data1 = np.fromstring(dec_data1, np.uint8)
    original = cv2.imdecode(np_data1, cv2.IMREAD_UNCHANGED)

    dec_data2 = base64.b64decode(data2)
    np_data2 = np.fromstring(dec_data2, np.uint8)
    image_to_compare = cv2.imdecode(np_data2, cv2.IMREAD_UNCHANGED)

# 1) Check if 2 images are equals
    if original.shape == image_to_compare.shape:
        difference = cv2.subtract(original, image_to_compare)
        b, g, r = cv2.split(difference)

        if cv2.countNonZero(b) == 0 and cv2.countNonZero(g) == 0 and cv2.countNonZero(r) == 0:
            return 100

# 2) Check for similarities between the 2 images
    sift = cv2.SIFT_create()
    kp_1, desc_1 = sift.detectAndCompute(original, None)
    kp_2, desc_2 = sift.detectAndCompute(image_to_compare, None)

    index_params = dict(algorithm=0, trees=5)
    search_params = dict()
    flann = cv2.FlannBasedMatcher(index_params, search_params)

    matches = flann.knnMatch(desc_1, desc_2, k=2)

    good_points = []
    for m, n in matches:
        if m.distance < 0.6*n.distance:
            good_points.append(m)

    # Define how similar they are
    number_keypoints = 0
    if len(kp_1) <= len(kp_2):
        number_keypoints = len(kp_1)
    else:
        number_keypoints = len(kp_2)

    percentage_similarity = float(len(good_points)) / number_keypoints * 100

    return percentage_similarity
    
    
