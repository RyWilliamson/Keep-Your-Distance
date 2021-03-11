// First flip byte order, then copy into a float to get correct float representation
int distance_bytes = (*(bytes + 0) << 24) | (*(bytes+1) << 16) | (*(bytes+2) << 8) | *(bytes + 3);
memcpy(&distance, &distance_bytes, 4);

// Flip byte order for measured_power and environment
measured_power = (*(bytes+4) << 24) | (*(bytes+5) << 16) | (*(bytes+6) << 8) | *(bytes+7);
environment = (*(bytes+8) << 24) | (*(bytes+9) << 16) | (*(bytes+10) << 8) | *(bytes+11);