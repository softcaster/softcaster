from math import sqrt 
def quadraticEquation(a,b,c):
    # Negative discriminant: Two complex solutions (involving imaginary numbers). 
    # Zero discriminant: One real solution (a repeated root).
    # Positive discriminant: Two distinct real solutions.

    discriminant =  b**2-4*c
    if(discriminant < 0.):
        print("Equa tion doesn't have real solution")
        return None
    else:
         d = sqrt(discriminant) 
         root1 = (-b + d) / 2
         root2 = (-b - d) /2
         return root1, root2