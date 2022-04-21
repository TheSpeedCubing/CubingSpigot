package net.minecraft.server;

import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

// CraftBukkit start
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
// CraftBukkit end

public class TileEntityBeacon extends TileEntityContainer implements IUpdatePlayerListBox, IInventory {

    public static final MobEffectList[][] a = new MobEffectList[][] { { MobEffectList.FASTER_MOVEMENT, MobEffectList.FASTER_DIG}, { MobEffectList.RESISTANCE, MobEffectList.JUMP}, { MobEffectList.INCREASE_DAMAGE}, { MobEffectList.REGENERATION}};
    private boolean i;
    private int j = -1;
    private int k;
    private int l;
    private ItemStack inventorySlot;
    private String n;
    // CraftBukkit start - add fields and methods
    public List<HumanEntity> transaction = new java.util.ArrayList<HumanEntity>();
    public boolean isEnabled() {
        return i;
    }
    public void setEnabled(boolean state) {
        this.i = state;
    }
    public int getLevel() {
        return j;
    }
    public void setLevel(int newLevel) {
        this.j = newLevel;
    }
    private int maxStack = MAX_STACK;

    public ItemStack[] getContents() {
        return new ItemStack[] { this.inventorySlot };
    }

    public void onOpen(CraftHumanEntity who) {
        transaction.add(who);
    }

    public void onClose(CraftHumanEntity who) {
        transaction.remove(who);
    }

    public List<HumanEntity> getViewers() {
        return transaction;
    }

    public void setMaxStackSize(int size) {
        maxStack = size;
    }
    // CraftBukkit end

    public TileEntityBeacon() {}

    public void c() {
        if (this.world.getTime() % 80L == 0L) {
            this.m();
        }

    }

    public void m() {
        this.B();
        this.A();
    }

    private void A() {
        if (isEnabled() && getLevel() > 0 && !this.world.isClientSide && this.k > 0) {
            double radius = getLevel() * 10 + 10;
            byte b0 = 0;

            if (getLevel() >= 4 && this.k == this.l) {
                b0 = 1;
            }

            int i = this.position.getX();
            int j = this.position.getY();
            int k = this.position.getZ();
            AxisAlignedBB axisalignedbb = new AxisAlignedBB(i, j, k, i + 1, j + 1, k + 1)
            .grow(radius, radius, radius).a(0.0D, this.world.getHeight(), 0.0D);
            List<EntityHuman> list = this.world.a(EntityHuman.class, axisalignedbb);
            for (EntityHuman entityhuman : list) {
                entityhuman.addEffect(new MobEffect(this.k, 180, b0, true, true));
            }

            if (getLevel() >= 4 && this.k != this.l && this.l > 0) {
            for (EntityHuman entityhuman : list) {
                    entityhuman.addEffect(new MobEffect(this.l, 180, 0, true, true));
                }
            }
        }

    }

    private void B() {
        int j = this.position.getX();
        int k = this.position.getY();
        int l = this.position.getZ();
        int prevLevel = getLevel();
        setLevel(0);
        setEnabled(true);
        BlockPosition.MutableBlockPosition mutableBlockPosition = new BlockPosition.MutableBlockPosition();
        for (int y = k + 1; y < 256; ++y) {
            Block block = this.world.getType(mutableBlockPosition.c(j, y, l)).getBlock();
            if (block != Blocks.STAINED_GLASS && block != Blocks.STAINED_GLASS_PANE && block.p() >= 15 && block != Blocks.BEDROCK) {
                setEnabled(false);
                        break;
                    }
        }

            if(isEnabled()){
                for (int i1 = 1; i1 <= 4; setLevel(i1++)) {
                int j1 = k - i1;

                if (j1 < 0) {
                    break;
                }

                boolean flag1 = true;

                for (int k1 = j - i1; k1 <= j + i1 && flag1; ++k1) {
                    for (int l1 = l - i1; l1 <= l + i1; ++l1) {
                        Block block = this.world.getType(new BlockPosition(k1, j1, l1)).getBlock();

                        if (block != Blocks.EMERALD_BLOCK && block != Blocks.GOLD_BLOCK && block != Blocks.DIAMOND_BLOCK && block != Blocks.IRON_BLOCK) {
                            flag1 = false;
                            break;
                        }
                    }
                }

                if (!flag1) {
                    break;
                }
            }

            if (getLevel()==0) {
                this.i = false;
            }
        }

        if (!this.world.isClientSide && getLevel() == 4 && prevLevel < getLevel()) {
            AxisAlignedBB bb = new AxisAlignedBB(j, k, l, j, k - 4, l).grow(10.0D, 5.0D, 10.0D);
            for (EntityHuman entityhuman : this.world.a(EntityHuman.class, bb)) {
                entityhuman.b((Statistic) AchievementList.K);
            }
        }

    }

    public Packet getUpdatePacket() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        this.b(nbttagcompound);
        return new PacketPlayOutTileEntityData(this.position, 3, nbttagcompound);
    }

    private int h(int i) {
        if (i >= 0 && i < MobEffectList.byId.length && MobEffectList.byId[i] != null) {
            MobEffectList mobeffectlist = MobEffectList.byId[i];

            return mobeffectlist != MobEffectList.FASTER_MOVEMENT && mobeffectlist != MobEffectList.FASTER_DIG && mobeffectlist != MobEffectList.RESISTANCE && mobeffectlist != MobEffectList.JUMP && mobeffectlist != MobEffectList.INCREASE_DAMAGE && mobeffectlist != MobEffectList.REGENERATION ? 0 : i;
        } else {
            return 0;
        }
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.k = this.h(nbttagcompound.getInt("Primary"));
        this.l = this.h(nbttagcompound.getInt("Secondary"));
        setLevel(nbttagcompound.getInt("Levels"));
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setInt("Primary", this.k);
        nbttagcompound.setInt("Secondary", this.l);
        nbttagcompound.setInt("Levels", getLevel());
    }

    public int getSize() {
        return 1;
    }

    public ItemStack getItem(int i) {
        return i == 0 ? this.inventorySlot : null;
    }

    public ItemStack splitStack(int i, int j) {
        if (i == 0 && this.inventorySlot != null) {
            if (j >= this.inventorySlot.count) {
                ItemStack itemstack = this.inventorySlot;

                this.inventorySlot = null;
                return itemstack;
            } else {
                this.inventorySlot.count -= j;
                return new ItemStack(this.inventorySlot.getItem(), j, this.inventorySlot.getData());
            }
        } else {
            return null;
        }
    }

    public ItemStack splitWithoutUpdate(int i) {
        if (i == 0 && this.inventorySlot != null) {
            ItemStack itemstack = this.inventorySlot;

            this.inventorySlot = null;
            return itemstack;
        } else {
            return null;
        }
    }

    public void setItem(int i, ItemStack itemstack) {
        if (i == 0) {
            this.inventorySlot = itemstack;
        }

    }

    public String getName() {
        return this.hasCustomName() ? this.n : "container.beacon";
    }

    public boolean hasCustomName() {
        return this.n != null && this.n.length() > 0;
    }

    public void a(String s) {
        this.n = s;
    }

    public int getMaxStackSize() {
        return maxStack; // CraftBukkit
    }

    public boolean a(EntityHuman entityhuman) {
        return this.world.getTileEntity(this.position) != this ? false : entityhuman.e((double) this.position.getX() + 0.5D, (double) this.position.getY() + 0.5D, (double) this.position.getZ() + 0.5D) <= 64.0D;
    }

    public void startOpen(EntityHuman entityhuman) {}

    public void closeContainer(EntityHuman entityhuman) {}

    public boolean b(int i, ItemStack itemstack) {
        return itemstack.getItem() == Items.EMERALD || itemstack.getItem() == Items.DIAMOND || itemstack.getItem() == Items.GOLD_INGOT || itemstack.getItem() == Items.IRON_INGOT;
    }

    public String getContainerName() {
        return "minecraft:beacon";
    }

    public Container createContainer(PlayerInventory playerinventory, EntityHuman entityhuman) {
        return new ContainerBeacon(playerinventory, this);
    }

    public int getProperty(int i) {
        switch (i) {
        case 0:

            return getLevel();
        case 1:
            return this.k;

        case 2:
            return this.l;

        default:
            return 0;
        }
    }

    public void b(int i, int j) {
        switch (i) {
        case 0:
            setLevel(j);
            break;

        case 1:
            this.k = this.h(j);
            break;

        case 2:
            this.l = this.h(j);
        }

    }

    public int g() {
        return 3;
    }

    public void l() {
        this.inventorySlot = null;
    }

    public boolean c(int i, int j) {
        if (i == 1) {
            this.m();
            return true;
        } else {
            return super.c(i, j);
        }
    }

}
