package teamreborn.assembly.powernet;


import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class Powernet {

    World world;
    Graph graph;

    public Powernet(World world) {
        this.world = world;
        graph = new Graph();
        //TODO read powernet
    }

    //Adds a connector to the current world
    public void addConnector(PowernetConnector connector) {
        for (Direction facing : Direction.values()) {
            if (isConnector(connector.getConnectorPos().offset(facing), connector.getConnectorWorld())) {
                graph.addLine(connector.getConnectorPos().asLong(), connector.getConnectorPos().offset(facing).asLong());
            }
        }
    }

    //Removes the connector from the graph, and handles removing the connections
    public void removeConnector(PowernetConnector connector) {
        graph.removeNode(connector.getConnectorPos().asLong());
    }

    public boolean isConnector(BlockPos pos, World world) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        return blockEntity instanceof PowernetConnector;
    }

    public void simulate() {
        if (world.dimension.getType().getRawId() == 0) {
            //TODO remove this
            // System.out.println(graph.buildNetworkMap().size() + " networks");
        }
    }


}
