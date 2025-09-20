import matplotlib.pyplot as plt
from mpl_toolkits.mplot3d import Axes3D
import numpy as np

v1 = np.array([1, -1, 3])
x1, y1, z1 = v1
v2 = np.array([-2, 1, 2])
x2, y2, z2 = v2
v3 = np.array([-1, -2, 1])
x3, y3, z3 = v3

fig = plt.figure()
ax = fig.add_subplot(111, projection='3d', facecolor='white')  # Weißer Hintergrund

ax.quiver(0, 0, 0, x1, y1, z1, color='red', arrow_length_ratio=0.2)
ax.quiver(0, 0, 0, x2, y2, z2, color='blue', arrow_length_ratio=0.2)
ax.quiver(0, 0, 0, x3, y3, z3, color='green', arrow_length_ratio=0.2)

# Lot von der Spitze des Vektors zur XY-Ebene (z = 0)
ax.plot([x1, x1], [y1, y1], [z1, 0], color='red', linestyle='dotted')
ax.plot([x2, x2], [y2, y2], [z2, 0], color='blue', linestyle='dotted')
ax.plot([x3, x3], [y3, y3], [z3, 0], color='green', linestyle='dotted')

# Achsenbereich festlegen
ax.set_xlim([-2, 2])
ax.set_ylim([-2, 2])
ax.set_zlim([0, 4])

# Achsenbeschriftung
ax.set_xlabel('x')
ax.set_ylabel('y')
ax.set_zlabel('z')

# Manuelle X- und Y-Achsen durch den Ursprung zeichnen
for axis in ['x', 'y']:
    line = np.zeros((2, 3))
    if axis == 'x':
        line[:, 0] = [-2, 2]
    elif axis == 'y':
        line[:, 1] = [-2, 2]
    ax.plot(line[:, 0], line[:, 1], line[:, 2], color='gray', linestyle='-')

plt.tight_layout()
plt.show()