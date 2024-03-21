import csv
import random
def generate_csv(filename, num_rows):
    with open(filename, 'w', newline='') as csvfile:
        writer = csv.writer(csvfile)
        writer.writerow(['Firs Column', 'Second Column'])  # Writing header
        for i in range(num_rows):
            non_unique_integer = random.randint(1, 100)  # Generate non-unique integer values
            second_column_value = random.random() * 1000  # Generating random values for the second column
            writer.writerow([non_unique_integer, second_column_value])


filename = 'generated_data.csv'  # Name of the CSV file to be generated
num_rows = 100_000  # Number of rows in the CSV file
generate_csv(filename, num_rows)
print(f"CSV file '{filename}' generated successfully with {num_rows} rows.")