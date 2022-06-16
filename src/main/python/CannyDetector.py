import numpy as np
import cv2
from PIL import Image
import base64
import io

def main(data):
    dec_data = base64.b64decode(data)
    np_data = np.fromstring(dec_data, np.uint8)
    img = cv2.imdecode(np_data, cv2.IMREAD_UNCHANGED)

    edge_img = cv2.Canny(img,100,200)

    pil_im = Image.fromarray(edge_img)
    buff = io.BytesIO()
    pil_im.save(buff, format = "PNG")
    img_str = base64.b64encode(buff.getvalue())
    return "" + str(img_str, 'utf-8')

