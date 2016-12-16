package t.a.m.com.doch1.Models.typeSerializer;

import com.activeandroid.serializer.TypeSerializer;

import t.a.m.com.doch1.Models.ListOfLongs;

/**
 * Created by Morad on 12/16/2016.
 */
public class ListOfLongsSerializer extends TypeSerializer {

    private static final String SEPERATOR = ",";

    @Override
    public Class<?> getDeserializedType() {
        return ListOfLongs.class;
    }

    @Override
    public Class<?> getSerializedType() {
        return String.class;
    }

    @Override
    public Object serialize(Object data) {

        if(data != null && data instanceof ListOfLongs){
            ListOfLongs longs = (ListOfLongs) data;
            StringBuilder builder = new StringBuilder();
            for (int index = 0; index < longs.size(); index++){
                builder.append(longs.get(index));

                if(index < longs.size() - 1){
                    builder.append(SEPERATOR);
                }
            }
            return builder.toString();
        }

        return data;
    }

    @Override
    public Object deserialize(Object data) {
        if(data != null){
            ListOfLongs longs = new ListOfLongs();
            String[] splits = ((String) data).split(SEPERATOR);

            for (int index = 0; index < splits.length; index++){
                longs.add(Long.parseLong(splits[index]));
            }
            return longs;
        }
        return null;
    }
}
