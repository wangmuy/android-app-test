import numpy as np
from scipy import linalg
def main():
    A = np.array([[1,2], [4,3]])
    B = linalg.inv(A)
    print(B)