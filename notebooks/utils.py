import os
import csv


def load_df_csv(df, items_id_to_save, filename):

    aux_df = df[df["item_id"].isin(items_id_to_save)]

    path = f'../ProcessedData/metadata/{filename}'

    os.makedirs(path, exist_ok=True)

    with open(f'{path}/{filename}-objects.csv', 'w') as f:
        # using csv.writer method from CSV package
        write = csv.writer(f)

        write.writerow(aux_df["item_id"].tolist())

    aux_df.iloc[:, 1:].to_csv(f'{path}/{filename}-relations.csv', header=False, index=False)

    with open(f'{path}/{filename}-attributes.csv', 'w') as f:
        # using csv.writer method from CSV package
        write = csv.writer(f)

        write.writerow(aux_df.iloc[:, 1:].columns.values.tolist())
