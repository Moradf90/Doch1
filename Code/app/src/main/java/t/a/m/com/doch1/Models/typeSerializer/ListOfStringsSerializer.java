package t.a.m.com.doch1.Models.typeSerializer;

import com.activeandroid.serializer.TypeSerializer;

import t.a.m.com.doch1.Models.ListOfLongs;
import t.a.m.com.doch1.Models.ListOfStrings;

/**
 * Created by Morad on 12/23/2016.
 */
public class ListOfStringsSerializer extends TypeSerializer {
    private static final String SEPERATOR = "_";

    @Override
    public Class<?> getDeserializedType() {
        return ListOfStrings.class;
    }

    @Override
    public Class<?> getSerializedType() {
        return String.class;
    }

    @Override
    public Object serialize(Object data) {

        if(data != null && data instanceof ListOfStrings){
            ListOfStrings strings = (ListOfStrings) data;
            StringBuilder builder = new StringBuilder();
            for (int index = 0; index < strings.size(); index++){
                builder.append(strings.get(index));

                if(index < strings.size() - 1){
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
            ListOfStrings longs = new ListOfStrings();
            String[] splits = ((String) data).split(SEPERATOR);

            for (int index = 0; index < splits.length; index++){
                longs.add(splits[index]);
            }

            return longs;
        }
        return null;
    }
}
