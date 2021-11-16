package platformer;

class Door extends Platform {
	Room room1, room2;

	public Door(int X, int Y, boolean vertical, Room r1, Room r2) {
		super(X, Y, 4, 8, platformType.DOOR);
		if (vertical) {
			w = 8 * 20;
			h = 4 * 20;
		}
		room1 = r1;
		room2 = r2;
		System.out.println(getRoom1());
		System.out.println(getRoom2());
	}

	public Room getRoom1() {
		return room1;
	}

	public Room getRoom2() {
		return room2;
	}
}