# import packages
from PIL import Image
import imagehash

# First pair
hash1 = imagehash.average_hash(Image.open('1_b.jpeg'))
hash2 = imagehash.average_hash(Image.open('5_b.jpg'))
diff = hash1 - hash2
print("result: ",diff)